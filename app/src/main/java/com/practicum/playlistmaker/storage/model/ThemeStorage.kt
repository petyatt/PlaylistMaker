package com.practicum.playlistmaker.storage.model

interface ThemeStorage {

    fun getTheme(): Boolean

    fun changeTheme(changed: Boolean)
}