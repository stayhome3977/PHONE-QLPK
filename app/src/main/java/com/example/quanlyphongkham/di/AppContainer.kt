package com.example.quanlyphongkham.di

import android.content.Context
import com.example.quanlyphongkham.BuildConfig
import com.example.quanlyphongkham.data.local.SessionStore
import com.example.quanlyphongkham.data.remote.ApiService
import com.example.quanlyphongkham.data.remote.AppJson
import com.example.quanlyphongkham.data.remote.AuthInterceptor
import com.example.quanlyphongkham.data.remote.RefreshApi
import com.example.quanlyphongkham.data.remote.TokenAuthenticator
import com.example.quanlyphongkham.data.repository.AuthRepository
import com.example.quanlyphongkham.data.repository.ClinicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** Hand-rolled dependency graph; one instance lives in [com.example.quanlyphongkham.QlpkApp]. */
class AppContainer(context: Context) {
    val sessionStore = SessionStore(context)

    /** Message for the login screen after the app signs the user out on purpose. */
    val authNotice = MutableStateFlow<String?>(null)

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        redactHeader("Authorization")
    }

    // Generous timeouts: the Azure App Service can take a while to wake from a cold start.
    private val baseClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build()

    private val converter = AppJson.asConverterFactory("application/json".toMediaType())

    private val refreshApi: RefreshApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_ROOT)
        .client(baseClient)
        .addConverterFactory(converter)
        .build()
        .create(RefreshApi::class.java)

    private val apiClient = baseClient.newBuilder()
        .apply { interceptors().add(0, AuthInterceptor(sessionStore)) }
        .authenticator(TokenAuthenticator(sessionStore, refreshApi))
        .build()

    private val api: ApiService = Retrofit.Builder()
        .baseUrl(BuildConfig.API_ROOT)
        .client(apiClient)
        .addConverterFactory(converter)
        .build()
        .create(ApiService::class.java)

    val authRepository = AuthRepository(api, sessionStore)
    val clinicRepository = ClinicRepository(api, authRepository)
}
