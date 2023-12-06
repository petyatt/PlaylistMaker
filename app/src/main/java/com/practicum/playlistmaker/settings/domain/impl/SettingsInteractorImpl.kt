package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun changeTheme(changed: Boolean) {
        settingsRepository.changeTheme(changed)
    }

    override fun getTheme(): Boolean {
        return settingsRepository.getTheme()
    }
}