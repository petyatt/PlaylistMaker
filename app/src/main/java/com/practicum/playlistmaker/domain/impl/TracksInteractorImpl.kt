package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute{
            consumer.consume(repository.searchTracks(expression))
        }

    }

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getTracks(): ArrayList<Track> {
        return repository.getTracks()
    }

    override fun darkMode(darkTheme: Boolean) {
        repository.darkMode(darkTheme)
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        repository.switchTheme(darkThemeEnabled)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override fun previewUrl(): String {
        return repository.previewUrl()
    }

    override fun imageUrl(): String {
        return repository.imageUrl()
    }

    override fun collectionName(): String {
        return repository.collectionName()
    }

    override fun primaryGenreName(): String {
        return repository.primaryGenreName()
    }

    override fun releaseDate(): String {
        return repository.releaseDate()
    }

    override fun country(): String {
        return repository.country()
    }

    override fun trackTimeMillis(): Long {
        return repository.trackTimeMillis()
    }

    override fun trackName(): String {
        return repository.trackName()
    }

    override fun artistName(): String {
        return repository.artistName()
    }
}