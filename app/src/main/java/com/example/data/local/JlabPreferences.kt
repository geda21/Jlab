package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "jlab_settings")

class JlabPreferences(private val context: Context) {

    private val KEY_APP_LOCK = booleanPreferencesKey("app_lock_enabled")
    private val KEY_LOCK_TIMEOUT = intPreferencesKey("app_lock_timeout_minutes")
    private val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode") // "SYSTEM", "LIGHT", "DARK"
    private val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_completed")
    private val KEY_FOCUS_DEFAULT = intPreferencesKey("focus_default_minutes")
    private val KEY_FAVORITE_MODULES = stringSetPreferencesKey("user_favorite_modules")
    private val KEY_RECENT_MODULES = stringPreferencesKey("recent_modules_csv")
    private val KEY_WORKSPACE_VIEW_MODE = stringPreferencesKey("workspace_view_mode") // "GRID" or "LIST"
    private val KEY_USER_NAME = stringPreferencesKey("user_profile_name")
    private val KEY_USER_BIO = stringPreferencesKey("user_profile_bio")
    private val KEY_USER_AVATAR_URI = stringPreferencesKey("user_profile_avatar_uri")
    private val KEY_HOME_BANNER_URI = stringPreferencesKey("home_banner_uri")
    private val KEY_HOME_BANNER_POSITION = stringPreferencesKey("home_banner_position")

    val isAppLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_APP_LOCK] ?: false }
    val lockTimeoutMinutes: Flow<Int> = context.dataStore.data.map { it[KEY_LOCK_TIMEOUT] ?: 0 }
    val themeMode: Flow<String> = context.dataStore.data.map { it[KEY_THEME_MODE] ?: "SYSTEM" }
    // Screenshots are fully allowed across the app; flag is permanently disabled
    val isSecureScreenEnabled: Flow<Boolean> = flowOf(false)
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[KEY_ONBOARDING_DONE] ?: false }
    val defaultFocusMinutes: Flow<Int> = context.dataStore.data.map { it[KEY_FOCUS_DEFAULT] ?: 25 }
    val userName: Flow<String> = context.dataStore.data.map { it[KEY_USER_NAME] ?: "Geda" }
    val userBio: Flow<String> = context.dataStore.data.map { it[KEY_USER_BIO] ?: "Productivity Architect" }
    val userAvatarUri: Flow<String> = context.dataStore.data.map { it[KEY_USER_AVATAR_URI] ?: "" }
    val homeBannerUri: Flow<String> = context.dataStore.data.map { it[KEY_HOME_BANNER_URI] ?: "" }
    val homeBannerPosition: Flow<String> = context.dataStore.data.map { it[KEY_HOME_BANNER_POSITION] ?: "COVER" }

    suspend fun updateHomeBanner(bannerUri: String, position: String = "COVER") {
        context.dataStore.edit { prefs ->
            prefs[KEY_HOME_BANNER_URI] = bannerUri
            prefs[KEY_HOME_BANNER_POSITION] = position
        }
    }

    suspend fun clearHomeBanner() {
        context.dataStore.edit { prefs ->
            prefs[KEY_HOME_BANNER_URI] = ""
            prefs[KEY_HOME_BANNER_POSITION] = "COVER"
        }
    }
    // Default is strictly EMPTY - no pre-selected or fake favorites
    val favoriteModules: Flow<Set<String>> = context.dataStore.data.map { it[KEY_FAVORITE_MODULES] ?: emptySet() }
    val recentModules: Flow<List<String>> = context.dataStore.data.map {
        (it[KEY_RECENT_MODULES] ?: "").split(",").filter { id -> id.isNotBlank() }
    }
    val workspaceViewMode: Flow<String> = context.dataStore.data.map { it[KEY_WORKSPACE_VIEW_MODE] ?: "LIST" }

    suspend fun updateProfile(name: String, bio: String, avatarUri: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_BIO] = bio
            prefs[KEY_USER_AVATAR_URI] = avatarUri
        }
    }

    suspend fun recordModuleAccess(moduleId: String) {
        if (moduleId.isBlank()) return
        context.dataStore.edit { prefs ->
            val currentList = (prefs[KEY_RECENT_MODULES] ?: "")
                .split(",")
                .filter { it.isNotBlank() && it != moduleId }
                .toMutableList()
            currentList.add(0, moduleId)
            val trimmed = currentList.take(6).joinToString(",")
            prefs[KEY_RECENT_MODULES] = trimmed
        }
    }

    suspend fun setWorkspaceViewMode(mode: String) {
        context.dataStore.edit { it[KEY_WORKSPACE_VIEW_MODE] = mode }
    }

    suspend fun toggleFavoriteModule(moduleId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITE_MODULES] ?: emptySet()
            if (current.contains(moduleId)) {
                prefs[KEY_FAVORITE_MODULES] = current - moduleId
            } else {
                prefs[KEY_FAVORITE_MODULES] = current + moduleId
            }
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_APP_LOCK] = enabled }
    }

    suspend fun setLockTimeoutMinutes(minutes: Int) {
        context.dataStore.edit { it[KEY_LOCK_TIMEOUT] = minutes }
    }

    suspend fun setSecureScreenEnabled(enabled: Boolean) {
        // No-op: screenshots are always allowed
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_DONE] = completed }
    }

    suspend fun setDefaultFocusMinutes(minutes: Int) {
        context.dataStore.edit { it[KEY_FOCUS_DEFAULT] = minutes }
    }
}
