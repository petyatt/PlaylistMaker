package com.practicum.playlistmaker.playlist.search.data.impl

import com.practicum.playlistmaker.playlist.search.data.network.NetworkClient
import com.practicum.playlistmaker.playlist.search.data.dto.TrackDto
import com.practicum.playlistmaker.playlist.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.playlist.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.playlist.storage.model.TrackStorage
import com.practicum.playlistmaker.playlist.search.domain.api.SearchRepository
import com.practicum.playlistmaker.playlist.search.domain.models.Track
import com.practicum.playlistmaker.playlist.util.Resource

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackStorage: TrackStorage,
) : SearchRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(message = "Проверьте подключение к интернету")
            }
            200 -> {
                Resource.Success((response as TrackSearchResponse).results.map {
                    Track(
                        it.id,
                        it.trackId,
                        it.country,
                        it.trackName,
                        it.previewUrl,
                        it.artistName,
                        it.releaseDate,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.primaryGenreName
                    )
                })
            }
            else -> {
                Resource.Error(message = "Ошибка сервера")
            }
        }
    }

    override fun saveTrack(track: Track) {
        val trackDto = TrackDto(
            track.id,
            track.trackId,
            track.country,
            track.trackName,
            track.previewUrl,
            track.artistName,
            track.releaseDate,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.primaryGenreName
        )
        trackStorage.save(trackDto)
    }

    override fun getTracks(): ArrayList<Track> {
        val trackDto = trackStorage.get()
        val track = trackDto.map {
            Track(
                it.id,
                it.trackId,
                it.country,
                it.trackName,
                it.previewUrl,
                it.artistName,
                it.releaseDate,
                it.trackTimeMillis,
                it.getCoverArtwork(),
                it.collectionName,
                it.primaryGenreName
            )
        }
        return ArrayList(track)
    }

    override fun clearHistory() {
        trackStorage.clearHistory()
    }
}