package com.practicum.playlistmaker.media.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.model.Playlist
import java.io.File

class PlaylistAdapter(
    private val clickListener: OnPlaylistClickListener
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder = PlaylistViewHolder(parent)

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    fun interface OnPlaylistClickListener {
        fun clickListener(playlist: Playlist)
    }

    inner class PlaylistViewHolder(
        parent: ViewGroup,
    ): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
    ) {

        val playlistAlbum: ImageView = itemView.findViewById(R.id.imageView_album)
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
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(10)))
                    .into(playlistAlbum)
            } else {
                playlistAlbum.setImageResource(R.drawable.placeholder)
            }

            itemView.setOnClickListener { clickListener.clickListener(playlist) }
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

    override fun onViewRecycled(holder: PlaylistViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.playlistAlbum)
    }
}