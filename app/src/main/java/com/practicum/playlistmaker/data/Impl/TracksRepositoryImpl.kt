package com.practicum.playlistmaker.data.Impl

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.storage.model.TrackStorage
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackStorage: TrackStorage
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            val trackResponse = response as TrackSearchResponse
            trackResponse.results.map {
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
            }
        } else {
            emptyList()
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
        val track = trackDto.map { it
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

    override fun darkMode(darkTheme: Boolean) {
        trackStorage.darkMode(darkTheme)
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        trackStorage.switchTheme(darkThemeEnabled)
    }

    override fun clearHistory() {
        trackStorage.clearHistory()
    }

    override fun previewUrl(): String {
        return trackStorage.previewUrl()
    }

    override fun imageUrl(): String {
        return trackStorage.imageUrl()
    }

    override fun collectionName(): String {
        return trackStorage.collectionName()
    }

    override fun primaryGenreName(): String {
        return trackStorage.primaryGenreName()
    }

    override fun releaseDate(): String {
        return trackStorage.releaseDate()
    }

    override fun country(): String {
        return trackStorage.country()
    }

    override fun trackTimeMillis(): Long {
        return trackStorage.trackTimeMillis()
    }

    override fun trackName(): String {
        return trackStorage.trackName()
    }

    override fun artistName(): String {
        return trackStorage.artistName()
    }
}