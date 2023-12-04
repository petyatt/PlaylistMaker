package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsInteractorImpl(private val settingsRepositoryImpl: SettingsRepositoryImpl) :
    SettingsInteractor {
    override fun changeTheme(changed: Boolean) {
        settingsRepositoryImpl.changeTheme(changed)
    }

    override fun getTheme(): Boolean {
        return settingsRepositoryImpl.getTheme()
    }
}