package com.example.quanlyphongkham.data.remote

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response
import java.io.IOException

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T, val status: Int) : ApiResult<T>

    data class Failure(
        val message: String,
        val status: Int? = null,
        val errorCode: String? = null,
        val retryAfterSeconds: Long? = null,
    ) : ApiResult<Nothing>
}

inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(data), status)
    is ApiResult.Failure -> this
}

/** Runs a Retrofit call and never throws: every outcome becomes an [ApiResult] with a Vietnamese message. */
suspend fun <T> apiCall(block: suspend () -> Response<T>): ApiResult<T> {
    val response = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        return ApiResult.Failure(ErrorMessages.NETWORK)
    } catch (e: SerializationException) {
        return ApiResult.Failure(ErrorMessages.BAD_RESPONSE)
    } catch (e: Exception) {
        return ApiResult.Failure(ErrorMessages.UNKNOWN)
    }

    if (response.isSuccessful) {
        @Suppress("UNCHECKED_CAST")
        val body = response.body() ?: Unit as T
        return ApiResult.Success(body, response.code())
    }

    val raw = try {
        response.errorBody()?.string()
    } catch (e: IOException) {
        null
    }
    return parseProblem(response.code(), raw, response.headers()["Retry-After"]?.toLongOrNull())
}

/** Maps an ASP.NET ProblemDetails body ({title, detail, errorCode, errors}) to a [ApiResult.Failure]. */
fun parseProblem(status: Int, rawBody: String?, retryAfterSeconds: Long? = null): ApiResult.Failure {
    val problem: JsonObject? = rawBody
        ?.takeIf { it.isNotBlank() }
        ?.let { runCatching { AppJson.parseToJsonElement(it).jsonObject }.getOrNull() }

    fun field(vararg names: String): String? = names.firstNotNullOfOrNull { name ->
        (problem?.get(name) as? JsonPrimitive)?.takeIf { it.isString }?.content
    }

    val errorCode = field("errorCode", "error_code")
    val detail = field("detail")
    val firstValidationError = runCatching {
        problem?.get("errors")?.jsonObject?.values?.firstOrNull()?.jsonArray?.firstOrNull()?.jsonPrimitive?.content
    }.getOrNull()

    val message = ErrorMessages.forCode(errorCode)
        ?: ErrorMessages.forStatus(status, retryAfterSeconds, detail ?: firstValidationError)
    return ApiResult.Failure(message, status, errorCode, retryAfterSeconds)
}
