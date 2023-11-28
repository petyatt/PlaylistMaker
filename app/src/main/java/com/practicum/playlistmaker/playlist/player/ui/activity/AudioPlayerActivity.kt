package com.practicum.playlistmaker.playlist.player.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.playlist.player.domain.models.PlayerState
import com.practicum.playlistmaker.playlist.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.playlist.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: AudioPlayerViewModel
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.getParcelableExtra<Track>("track")

        viewModel = ViewModelProvider(this, AudioPlayerViewModel.getViewModelFactory())[AudioPlayerViewModel::class.java]

        viewModel.playerState.observe(this) { state ->
            when (state) {
                PlayerState.STATE_PLAYING -> {
                    binding.playButton.setImageResource(R.drawable.pause)
                }

                PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                    binding.playButton.setImageResource(R.drawable.play)
                }

                else -> {
                    PlayerState.STATE_DEFAULT
                }
            }
        }

        track?.previewUrl?.let { viewModel.preparePlayer(it) }

        viewModel.progressTimer.observe(this) { progress ->
            binding.playbackProgress.text = progress
            viewModel.progressRunnable.run()
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.tvYear.text = track?.releaseDate?.substring(0, 4)
        binding.tvCountry.text = track?.country
        binding.tvDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)
        binding.tvGenre.text = track?.primaryGenreName
        binding.tvAlbum.text = track?.collectionName
        binding.textView14.text = track?.trackName
        binding.textView15.text = track?.artistName

        Glide.with(this)
            .load(track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(30))
            .into(binding.imageView6)

        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.playerState.value == PlayerState.STATE_PLAYING) {
            viewModel.startPlayer()
            binding.playButton.setImageResource(R.drawable.pause)
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.playerState.value == PlayerState.STATE_PLAYING) {
            viewModel.pausePlayer()
            binding.playButton.setImageResource(R.drawable.play)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }
}