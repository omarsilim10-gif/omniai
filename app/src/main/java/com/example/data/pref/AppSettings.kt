package com.example.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    SYSTEM, DARK, LIGHT
}

enum class NetworkPreference {
    AUTO, ONLINE_ONLY, OFFLINE_ONLY
}

data class AppConfig(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val language: String = "ar", // "ar" or "en"
    val apiKey: String = "",
    val customBaseUrl: String = "https://generativelanguage.googleapis.com/",
    val networkPreference: NetworkPreference = NetworkPreference.AUTO,
    val fastModel: String = "gemini-3.5-flash",
    val thinkingModel: String = "gemini-3.1-pro-preview",
    val imageModel: String = "gemini-2.5-flash-image",
    val videoModel: String = "veo-3.1-fast-generate-preview"
)

class AppSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("omni_ai_settings", Context.MODE_PRIVATE)

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<AppConfig> = _config.asStateFlow()

    private fun loadConfig(): AppConfig {
        val themeStr = prefs.getString("theme_mode", AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
        val theme = runCatching { AppThemeMode.valueOf(themeStr) }.getOrDefault(AppThemeMode.DARK)
        val lang = prefs.getString("language", "ar") ?: "ar"
        val customKey = prefs.getString("custom_api_key", "") ?: ""
        val baseUrl = prefs.getString("custom_base_url", "https://generativelanguage.googleapis.com/")
            ?: "https://generativelanguage.googleapis.com/"
        val netPrefStr = prefs.getString("net_pref", NetworkPreference.AUTO.name) ?: NetworkPreference.AUTO.name
        val netPref = runCatching { NetworkPreference.valueOf(netPrefStr) }.getOrDefault(NetworkPreference.AUTO)
        val fastMod = prefs.getString("fast_model", "gemini-3.5-flash") ?: "gemini-3.5-flash"
        val thinkMod = prefs.getString("think_model", "gemini-3.1-pro-preview") ?: "gemini-3.1-pro-preview"

        return AppConfig(
            themeMode = theme,
            language = lang,
            apiKey = customKey,
            customBaseUrl = baseUrl,
            networkPreference = netPref,
            fastModel = fastMod,
            thinkingModel = thinkMod
        )
    }

    fun getEffectiveApiKey(): String {
        val custom = _config.value.apiKey.trim()
        if (custom.isNotEmpty()) return custom
        return BuildConfig.GEMINI_API_KEY
    }

    fun setTheme(theme: AppThemeMode) {
        prefs.edit().putString("theme_mode", theme.name).apply()
        _config.value = _config.value.copy(themeMode = theme)
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
        _config.value = _config.value.copy(language = lang)
    }

    fun setApiKey(key: String) {
        prefs.edit().putString("custom_api_key", key).apply()
        _config.value = _config.value.copy(apiKey = key)
    }

    fun setCustomBaseUrl(url: String) {
        prefs.edit().putString("custom_base_url", url).apply()
        _config.value = _config.value.copy(customBaseUrl = url)
    }

    fun setNetworkPreference(pref: NetworkPreference) {
        prefs.edit().putString("net_pref", pref.name).apply()
        _config.value = _config.value.copy(networkPreference = pref)
    }

    fun setModels(fast: String, think: String) {
        prefs.edit().putString("fast_model", fast).putString("think_model", think).apply()
        _config.value = _config.value.copy(fastModel = fast, thinkingModel = think)
    }
}
