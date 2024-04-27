package com.practicum.playlistmaker.media.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Playlist(
    val playlistId: Long = 0,
    val title: String,
    val description: String,
    val imagePath: String?,
    var trackCount: Int
): Parcelable, Serializable