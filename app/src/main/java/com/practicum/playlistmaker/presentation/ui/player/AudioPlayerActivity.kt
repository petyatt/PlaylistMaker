package com.practicum.playlistmaker.presentation.ui.player


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.presentation.ui.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.ui.track.SearchTrackActivity
import com.practicum.playlistmaker.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private var playerState = STATE_DEFAULT
    private var isAppInBackground = false
    private var mainThreadHandler: Handler? = null
    private val mediaPlayer = Creator.providePlayerInteractor()

    private var playButton: ImageButton? = null
    private lateinit var artworkUrl100: ImageView
    private lateinit var tvCollectionName: TextView
    private lateinit var tvPrimaryGenreName: TextView
    private lateinit var tvCountry: TextView
    private lateinit var tvReleaseDate: TextView
    private lateinit var tvTrackTimeMillis: TextView
    private lateinit var tvTrackName: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var playbackProgress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val track = Creator.provideTracksInteractor(this)

        mainThreadHandler = Handler(Looper.getMainLooper())

        val app = application as App
        app.switchTheme(app.darkTheme)

        setContentView(R.layout.activity_player)

        initViews()

        fun preparePlayer() {
            mediaPlayer.setDataSource(track.previewUrl())
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playButton?.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                mediaPlayer.seekPlayer(INITIAL_POSITION)
                playButton?.setImageResource(R.drawable.play)
                playbackProgress.text = TIMER_START
                mainThreadHandler?.removeCallbacks(progressRunnable)
            }
        }

        fun startPlayer() {
            mediaPlayer.startPlayer()
            playerState = STATE_PLAYING
            mainThreadHandler?.postDelayed(progressRunnable, DELAY_MILLIS)
        }

        fun pausePlayer() {
            mediaPlayer.pausePlayer()
            playerState = STATE_PAUSED
        }

        fun playbackControl() {
            when(playerState) {
                STATE_PLAYING -> {
                    pausePlayer()
                    playButton?.setImageResource(R.drawable.play)
                }
                STATE_PREPARED, STATE_PAUSED -> {
                    startPlayer()
                    playButton?.setImageResource(R.drawable.pause)
                }
            }
        }

        preparePlayer()

        playButton?.setOnClickListener {
            playbackControl()
        }

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy", Locale.getDefault())

        fun String?.formatDate(): String? = try {
            this?.let {
                val parsedDate = inputFormat.parse(this)
                outputFormat.format(parsedDate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        tvReleaseDate.text = track.releaseDate().formatDate()
        tvCountry.text = track.country()
        tvTrackTimeMillis.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis())
        tvPrimaryGenreName.text = track.primaryGenreName()
        tvCollectionName.text = track.collectionName()
        tvTrackName.text = track.trackName()
        tvArtistName.text = track.artistName()



        Glide.with(this)
            .load(track.imageUrl())
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(30))
            .into(artworkUrl100)

        val imageButtonBack = findViewById<ImageButton>(R.id.button_back)
        imageButtonBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (playerState == STATE_PLAYING) {
            mediaPlayer.startPlayer()
            playButton?.setImageResource(R.drawable.pause)
            mainThreadHandler?.post(progressRunnable)
        }

        if (isAppInBackground) {
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        isAppInBackground = false
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaing()) {
            mediaPlayer.pausePlayer()
            playButton?.setImageResource(R.drawable.play)
            mainThreadHandler?.removeCallbacks(progressRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.releasePlayer()
    }

    private fun initViews() {
        playbackProgress = findViewById(R.id.playback_progress)
        playButton = findViewById(R.id.playButton)
        artworkUrl100 = findViewById(R.id.imageView6)
        tvCollectionName = findViewById(R.id.tvAlbum)
        tvPrimaryGenreName = findViewById(R.id.tvGenre)
        tvCountry = findViewById(R.id.tvCountry)
        tvReleaseDate = findViewById(R.id.tvYear)
        tvTrackTimeMillis = findViewById(R.id.tvDuration)
        tvTrackName = findViewById(R.id.textView14)
        tvArtistName = findViewById(R.id.textView15)
    }

    private val progressRunnable = object : Runnable {
        override fun run() {
            playbackProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.getCurrentPosition())
            mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val INITIAL_POSITION = 0
        private const val TIMER_START = "00:00"
        const val DELAY_MILLIS = 300L
    }
}