package com.practicum.playlistmaker.settings.data.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.storage.sharedPrefsStorage.SharedPrefsStorage

class SettingsRepositoryImpl(private val storageTheme: SharedPrefsStorage) :
    SettingsRepository {
    override fun changeTheme(changed: Boolean) {
        storageTheme.changeTheme(changed)
    }

    override fun getTheme(): Boolean {
        return storageTheme.getTheme()
    }
}