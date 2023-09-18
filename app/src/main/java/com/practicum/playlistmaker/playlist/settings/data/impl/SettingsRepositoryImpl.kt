package com.practicum.playlistmaker.playlist.settings.data.impl

import com.practicum.playlistmaker.playlist.storage.sharedprefs.SharedPrefsStorage
import com.practicum.playlistmaker.playlist.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val storageTheme: SharedPrefsStorage) :
    SettingsRepository {
    override fun changeTheme(changed: Boolean) {
        storageTheme.changeTheme(changed)
    }

    override fun getTheme(): Boolean {
        return storageTheme.getTheme()
    }
}