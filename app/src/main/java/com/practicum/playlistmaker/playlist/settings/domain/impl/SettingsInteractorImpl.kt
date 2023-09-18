package com.practicum.playlistmaker.playlist.settings.domain.impl

import com.practicum.playlistmaker.playlist.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.playlist.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.playlist.settings.domain.api.SettingsInteractor

class SettingsInteractorImpl(private val settingsRepositoryImpl: SettingsRepositoryImpl) :
    SettingsInteractor {
    override fun changeTheme(changed: Boolean) {
        settingsRepositoryImpl.changeTheme(changed)
    }

    override fun getTheme(): Boolean {
        return settingsRepositoryImpl.getTheme()
    }
}