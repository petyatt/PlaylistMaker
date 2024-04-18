package com.practicum.playlistmaker.media.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.model.Playlist
import java.io.File

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHoldrer>() {

    private var playlists: List<Playlist> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHoldrer = PlaylistViewHoldrer(parent)

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHoldrer, position: Int) {
        holder.bind(playlists[position])
    }

    inner class PlaylistViewHoldrer(
        parent: ViewGroup,
    ): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
    ) {

        private val playlistAlbum: ImageView = itemView.findViewById(R.id.imageView_album)
        private val playlistTitle: TextView = itemView.findViewById(R.id.textView_title)
        private val playlistCountTrack: TextView = itemView.findViewById(R.id.textView_count_track)

        fun bind(playlist: Playlist) {

            playlistTitle.text = playlist.title
            val trackWord = getTracksWord(playlist.trackCount)
            playlistCountTrack.text = itemView.context.getString(R.string.tracks_count, playlist.trackCount, trackWord)


            if (playlist.imagePath?.isNotEmpty() == true) {
                Glide.with(itemView.context)
                    .load(File(playlist.imagePath))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
                    .transform(RoundedCorners(30))
                    .into(playlistAlbum)
            } else {
                playlistAlbum.setImageResource(R.drawable.placeholder)
            }
        }
    }

    fun getTracksWord(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "трек"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "трека"
            else -> "треков"
        }
    }

    fun setPlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}