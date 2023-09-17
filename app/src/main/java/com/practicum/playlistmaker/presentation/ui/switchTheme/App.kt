package com.practicum.playlistmaker.presentation.ui.switchTheme

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPrefs.edit()
            .putBoolean(DARK_THEME, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val PREFS = "prefs"
        const val DARK_THEME = "dark_theme"
    }
}