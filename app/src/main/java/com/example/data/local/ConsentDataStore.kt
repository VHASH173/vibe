package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.consentDataStore: DataStore<Preferences> by preferencesDataStore(name = "vibe_consent_prefs")

data class LegalConsentRecord(
    val isRegistered: Boolean,
    val termsAcceptedVersion: String,
    val termsAcceptedTimestamp: Long,
    val privacyAccepted: Boolean,
    val userEmail: String,
    val username: String
)

data class StreamerProfileData(
    val displayName: String = "Álex Streamer",
    val handle: String = "@alex_vibe",
    val bio: String = "Creador Oficial en VibeStream • Transmisiones diarias con filtros AR 🚀",
    val avatarUri: String? = null,
    val avatarEmoji: String = "👑",
    val category: String = "VTuber / Gaming",
    val isVerified: Boolean = true,
    val followersCount: String = "24.8K",
    val followingCount: String = "342",
    val likesCount: String = "182K",
    val syncStatus: String = "Firebase Sincronizado"
)

class ConsentRepository(private val context: Context) {
    companion object {
        val KEY_IS_REGISTERED = booleanPreferencesKey("is_registered")
        val KEY_TERMS_VERSION = stringPreferencesKey("terms_accepted_version")
        val KEY_TERMS_TIMESTAMP = longPreferencesKey("terms_accepted_timestamp")
        val KEY_PRIVACY_ACCEPTED = booleanPreferencesKey("privacy_accepted")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USERNAME = stringPreferencesKey("user_username")
        val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode")
        
        // Streamer Profile Preferences
        val KEY_DISPLAY_NAME = stringPreferencesKey("profile_display_name")
        val KEY_PROFILE_HANDLE = stringPreferencesKey("profile_handle")
        val KEY_PROFILE_BIO = stringPreferencesKey("profile_bio")
        val KEY_PROFILE_AVATAR_URI = stringPreferencesKey("profile_avatar_uri")
        val KEY_PROFILE_AVATAR_EMOJI = stringPreferencesKey("profile_avatar_emoji")
        val KEY_PROFILE_CATEGORY = stringPreferencesKey("profile_category")

        const val CURRENT_TERMS_VERSION = "v1.0"
    }

    val themeModeFlow: Flow<String> = context.consentDataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "DARK" // Default to Dark for streaming aesthetic
    }

    suspend fun setThemeMode(mode: String) {
        context.consentDataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }

    val consentFlow: Flow<LegalConsentRecord> = context.consentDataStore.data.map { prefs ->
        LegalConsentRecord(
            isRegistered = prefs[KEY_IS_REGISTERED] ?: false,
            termsAcceptedVersion = prefs[KEY_TERMS_VERSION] ?: "",
            termsAcceptedTimestamp = prefs[KEY_TERMS_TIMESTAMP] ?: 0L,
            privacyAccepted = prefs[KEY_PRIVACY_ACCEPTED] ?: false,
            userEmail = prefs[KEY_USER_EMAIL] ?: "",
            username = prefs[KEY_USERNAME] ?: ""
        )
    }

    val streamerProfileFlow: Flow<StreamerProfileData> = context.consentDataStore.data.map { prefs ->
        StreamerProfileData(
            displayName = prefs[KEY_DISPLAY_NAME] ?: "Álex Streamer",
            handle = prefs[KEY_PROFILE_HANDLE] ?: "@alex_vibe",
            bio = prefs[KEY_PROFILE_BIO] ?: "Creador Oficial en VibeStream • Transmisiones diarias con filtros AR 🚀",
            avatarUri = prefs[KEY_PROFILE_AVATAR_URI],
            avatarEmoji = prefs[KEY_PROFILE_AVATAR_EMOJI] ?: "👑",
            category = prefs[KEY_PROFILE_CATEGORY] ?: "VTuber / Gaming",
            isVerified = true,
            syncStatus = "Firebase Cloud Sincronizado"
        )
    }

    suspend fun updateStreamerProfile(
        displayName: String,
        handle: String,
        bio: String,
        avatarUri: String?,
        avatarEmoji: String,
        category: String
    ) {
        context.consentDataStore.edit { prefs ->
            prefs[KEY_DISPLAY_NAME] = displayName
            prefs[KEY_PROFILE_HANDLE] = handle
            prefs[KEY_PROFILE_BIO] = bio
            if (avatarUri != null) {
                prefs[KEY_PROFILE_AVATAR_URI] = avatarUri
            } else {
                prefs.remove(KEY_PROFILE_AVATAR_URI)
            }
            prefs[KEY_PROFILE_AVATAR_EMOJI] = avatarEmoji
            prefs[KEY_PROFILE_CATEGORY] = category
        }
    }

    suspend fun saveRegistrationConsent(
        email: String,
        username: String,
        termsVersion: String = CURRENT_TERMS_VERSION,
        timestamp: Long = System.currentTimeMillis()
    ) {
        context.consentDataStore.edit { prefs ->
            prefs[KEY_IS_REGISTERED] = true
            prefs[KEY_TERMS_VERSION] = termsVersion
            prefs[KEY_TERMS_TIMESTAMP] = timestamp
            prefs[KEY_PRIVACY_ACCEPTED] = true
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USERNAME] = username
        }
    }

    suspend fun getConsentOnce(): LegalConsentRecord {
        return consentFlow.first()
    }

    suspend fun getProfileOnce(): StreamerProfileData {
        return streamerProfileFlow.first()
    }

    /**
     * Derecho al Olvido (GDPR/CCPA): Limpia completamente el DataStore
     */
    suspend fun clearConsentData() {
        context.consentDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
