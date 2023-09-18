package com.practicum.playlistmaker.playlist.search.domain.impl

import Track
import com.practicum.playlistmaker.playlist.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.playlist.search.domain.api.SearchRepository
import com.practicum.playlistmaker.playlist.util.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute{
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> {consumer.consume(null, resource.message)}
            }
        }

    }

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getTracks(): ArrayList<Track> {
        return repository.getTracks()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}