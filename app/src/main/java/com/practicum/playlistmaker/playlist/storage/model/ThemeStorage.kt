package com.practicum.playlistmaker.playlist.storage.model

interface ThemeStorage {

    fun getTheme(): Boolean

    fun changeTheme(changed: Boolean)
}