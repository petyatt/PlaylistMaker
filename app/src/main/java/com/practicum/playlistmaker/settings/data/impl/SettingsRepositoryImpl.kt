package com.practicum.playlistmaker.settings.data.impl

import com.practicum.playlistmaker.settings.data.localstorage.LocalThemeStorage
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val storageTheme: LocalThemeStorage) :
    SettingsRepository {
    override fun changeTheme(changed: Boolean) {
        storageTheme.changeTheme(changed)
    }

    override fun getTheme(): Boolean {
        return storageTheme.getTheme()
    }
}