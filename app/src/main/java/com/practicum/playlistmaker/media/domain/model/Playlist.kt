package com.practicum.playlistmaker.media.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val playlistId: Long = 0,
    val title: String,
    val description: String,
    val imagePath: String?,
    var trackList: List<Long?>,
    var trackCount: Int
): Parcelable