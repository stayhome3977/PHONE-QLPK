package com.example.quanlyphongkham.data.remote

import com.example.quanlyphongkham.data.local.SessionStore
import com.example.quanlyphongkham.data.remote.dto.RefreshTokenRequest
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

private const val AUTHORIZATION = "Authorization"

/** Attaches the current access token to every API request. */
class AuthInterceptor(private val sessionStore: SessionStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionStore.current?.accessToken
        val request = if (token != null && chain.request().header(AUTHORIZATION) == null) {
            chain.request().newBuilder().header(AUTHORIZATION, "Bearer $token").build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

/**
 * On a 401, rotates the refresh token once and replays the request. Concurrent 401s share one refresh:
 * whoever arrives after it finished simply picks up the new token.
 */
class TokenAuthenticator(
    private val sessionStore: SessionStore,
    private val refreshApi: RefreshApi,
) : Authenticator {
    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        val sentToken = response.request.header(AUTHORIZATION)?.removePrefix("Bearer ") ?: return null
        if (response.priorResponse != null) return null

        synchronized(lock) {
            val session = sessionStore.current ?: return null
            if (session.accessToken != sentToken) {
                return response.request.withToken(session.accessToken)
            }

            val refreshed = try {
                refreshApi.refresh(RefreshTokenRequest(session.refreshToken)).execute()
            } catch (e: IOException) {
                return null // Offline: keep the session, the caller shows a network error.
            }

            val tokens = refreshed.body()
            if (refreshed.isSuccessful && tokens != null) {
                sessionStore.saveBlocking(tokens)
                return response.request.withToken(tokens.accessToken)
            }

            // The refresh token was revoked or expired: sign the user out.
            sessionStore.clearBlocking()
            return null
        }
    }

    private fun Request.withToken(token: String): Request =
        newBuilder().header(AUTHORIZATION, "Bearer $token").build()
}
