package com.practicum.playlistmaker.playlist.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.playlist.storage.sharedprefs.SharedPrefsStorage

class App : Application() {

    var darkTheme: Boolean = false
    private lateinit var sharedPrefsStorage: SharedPrefsStorage
    override fun onCreate() {
        super.onCreate()

        sharedPrefsStorage = SharedPrefsStorage(applicationContext)
        darkTheme = sharedPrefsStorage.getTheme()
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPrefsStorage.changeTheme(darkTheme)

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}

