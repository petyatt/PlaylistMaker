package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(private val tracks: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pattern_track, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val trackNameTextView: TextView = itemView.findViewById(R.id.track_name)
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artist_name)
        private val trackTimeTextView: TextView = itemView.findViewById(R.id.track_time)
        private val artworkImageView: ImageView = itemView.findViewById(R.id.artwork)

        fun bind(tracks: Track) {
            trackNameTextView.text = tracks.trackName
            artistNameTextView.text = tracks.artistName
            trackTimeTextView.text = tracks.trackTime

            Glide.with(itemView)
                .load(tracks.artworkUrl100)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(10))
                .into(artworkImageView)
        }
    }
}