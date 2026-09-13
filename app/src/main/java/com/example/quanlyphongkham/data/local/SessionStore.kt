package com.example.quanlyphongkham.data.local

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.quanlyphongkham.data.remote.AppJson
import com.example.quanlyphongkham.data.remote.dto.AuthTokenResponse
import com.example.quanlyphongkham.data.remote.dto.AuthenticatedAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val account: AuthenticatedAccount,
)

/**
 * Persists the signed-in session. [session] is the single source of truth for "who is logged in":
 * the UI switches to the login flow whenever it becomes null (logout, failed refresh, password change).
 */
class SessionStore(context: Context) {
    private val dataStore = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile("qlpk_session") },
    )

    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()

    private val _loaded = MutableStateFlow(false)
    val loaded: StateFlow<Boolean> = _loaded.asStateFlow()

    /** Synchronous read for OkHttp interceptors. */
    val current: Session? get() = _session.value

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = dataStore.data.first()
            val access = prefs[ACCESS]
            val refresh = prefs[REFRESH]
            val account = prefs[ACCOUNT]?.let {
                runCatching { AppJson.decodeFromString<AuthenticatedAccount>(it) }.getOrNull()
            }
            if (access != null && refresh != null && account != null) {
                _session.value = Session(access, refresh, account)
            }
            _loaded.value = true
        }
    }

    suspend fun save(tokens: AuthTokenResponse) {
        val session = Session(tokens.accessToken, tokens.refreshToken, tokens.account)
        dataStore.edit {
            it[ACCESS] = session.accessToken
            it[REFRESH] = session.refreshToken
            it[ACCOUNT] = AppJson.encodeToString(session.account)
        }
        _session.value = session
    }

    suspend fun updateAccount(account: AuthenticatedAccount) {
        val existing = _session.value ?: return
        dataStore.edit { it[ACCOUNT] = AppJson.encodeToString(account) }
        _session.value = existing.copy(account = account)
    }

    /** Signs out; app-level flags such as the home-screen prompt survive. */
    suspend fun clear() {
        dataStore.edit {
            it.remove(ACCESS)
            it.remove(REFRESH)
            it.remove(ACCOUNT)
        }
        _session.value = null
    }

    fun saveBlocking(tokens: AuthTokenResponse) = runBlocking { save(tokens) }

    fun clearBlocking() = runBlocking { clear() }

    suspend fun pinPromptShown(): Boolean = dataStore.data.map { it[PIN_PROMPTED] ?: false }.first()

    suspend fun markPinPromptShown() {
        dataStore.edit { it[PIN_PROMPTED] = true }
    }

    private companion object {
        val ACCESS = stringPreferencesKey("access_token")
        val REFRESH = stringPreferencesKey("refresh_token")
        val ACCOUNT = stringPreferencesKey("account")
        val PIN_PROMPTED = booleanPreferencesKey("pin_prompted")
    }
}
