package com.practicum.playlistmaker.settings.data.localstorage

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.model.ThemeStorage

class LocalThemeStorage(private val sharedPreferences: SharedPreferences): ThemeStorage {

    companion object {
        const val THEME_SHARED_PREFERENCES = "themeSharedPreferences"
        const val DARK_THEME = "dark_theme"
    }

    override fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME, false)
    }

    override fun changeTheme(changed: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME, changed)
            .apply()
    }


}