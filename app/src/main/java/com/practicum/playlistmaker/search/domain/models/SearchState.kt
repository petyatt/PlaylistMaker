package com.practicum.playlistmaker.search.domain.models

sealed interface SearchState{

    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    object Error : SearchState

    object Empty : SearchState
}