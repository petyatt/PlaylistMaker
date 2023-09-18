package com.practicum.playlistmaker.presentation.ui.track

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui.player.AudioPlayerActivity
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(context: Context) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private val tracksInteractor = Creator.provideTracksInteractor(context)
    var tracks = tracksInteractor.getTracks()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AudioPlayerActivity::class.java)
            holder.itemView.context.startActivity(intent)
            tracksInteractor.saveTrack(track)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = tracks.size

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val trackNameTextView: TextView = itemView.findViewById(R.id.track_name)
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artist_name)
        private val trackTimeTextView: TextView = itemView.findViewById(R.id.track_time)
        private val artworkImageView: ImageView = itemView.findViewById(R.id.artwork)

        fun bind(track: Track) {
            trackNameTextView.text = track.trackName
            artistNameTextView.text = track.artistName
            trackTimeTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)


            Glide.with(itemView)
                .load(track.artworkUrl100)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(10))
                .into(artworkImageView)
        }
    }
}