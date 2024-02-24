package com.practicum.playlistmaker.search.data.impl

import androidx.annotation.StringRes
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.domain.models.TrackStorage
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackStorage: TrackStorage,
) : SearchRepository {

    @StringRes
    private val noInternetMessage: Int = R.string.error_not_internet
    @StringRes
    private val serverErrorMessage: Int = R.string.server_error_message

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
               emit(Resource.Error(message = noInternetMessage.toString()))
            }
            200 -> {
                with(response as TrackSearchResponse) {
                    val data = results.map {
                        Track(
                            it.trackId,
                            it.country,
                            it.trackName,
                            it.previewUrl,
                            it.artistName,
                            it.releaseDate,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.collectionName,
                            it.primaryGenreName,
                            it.isFavorite,
                            it.addedAt
                        )
                    }
                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error(message = serverErrorMessage.toString()))
            }
        }
    }

    override fun saveTrack(track: Track) {
        val trackDto = TrackDto(
            track.trackId,
            track.country,
            track.trackName,
            track.previewUrl,
            track.artistName,
            track.releaseDate,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.primaryGenreName,
            track.isFavorite,
            System.currentTimeMillis()
        )
        trackStorage.save(trackDto)
    }

    override fun getTracks(): ArrayList<Track> {
        val trackDto = trackStorage.get()
        val track = trackDto.map {
            Track(
                it.trackId,
                it.country,
                it.trackName,
                it.previewUrl,
                it.artistName,
                it.releaseDate,
                it.trackTimeMillis,
                it.getCoverArtwork(),
                it.collectionName,
                it.primaryGenreName,
                it.isFavorite,
                it.addedAt
            )
        }
        return ArrayList(track)
    }

    override fun clearHistory() {
        trackStorage.clearHistory()
    }
}