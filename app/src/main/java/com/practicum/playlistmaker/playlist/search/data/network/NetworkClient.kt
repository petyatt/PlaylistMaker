package com.practicum.playlistmaker.playlist.search.data.network

import com.practicum.playlistmaker.playlist.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}