package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var isAppInBackground = false
    private var mainThreadHandler: Handler? = null

    private lateinit var playButton: ImageButton
    private lateinit var artworkUrl100: ImageView
    private lateinit var tvCollectionName: TextView
    private lateinit var tvPrimaryGenreName: TextView
    private lateinit var tvCountry: TextView
    private lateinit var tvReleaseDate: TextView
    private lateinit var tvTrackTimeMillis: TextView
    private lateinit var tvTrackName: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var playbackProgress: TextView
    private lateinit var searchHistory: SearchHistory
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainThreadHandler = Handler(Looper.getMainLooper())

        val app = application as App
        app.switchTheme(app.darkTheme)

        setContentView(R.layout.activity_player)

        sharedPreferences = getSharedPreferences(SearchActivity.SAVE_HISTORY, Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        initViews()

        val previewUrl = sharedPreferences.getString(SearchHistory.PREVIEW_URL, null)
        val imageUrl  = sharedPreferences.getString(SearchHistory.ARTWORK_URL_100, null)
        val collectionName = sharedPreferences.getString(SearchHistory.COLLECTION_NAME, null)
        val primaryGenreName = sharedPreferences.getString(SearchHistory.PRIMARY_GENRE_NAME, null)
        val releaseDate = sharedPreferences.getString(SearchHistory.RELEASE_DATE, null)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val country = sharedPreferences.getString(SearchHistory.COUNTRY, null)
        val trackTimeMillis = sharedPreferences.getLong(SearchHistory.TRACK_TIME_MILLIS, 0)
        val trackName = sharedPreferences.getString(SearchHistory.TRACK_NAME, null)
        val artistName = sharedPreferences.getString(SearchHistory.ARTIST_NAME, null)

        fun preparePlayer() {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playButton.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                mediaPlayer.seekTo(0)
                playButton.setImageResource(R.drawable.play)
                playbackProgress.text = "00:00"
                mainThreadHandler?.removeCallbacks(progressRunnable)
            }
        }

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()

            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.play)
            } else {
                mediaPlayer.start()
                playButton.setImageResource(R.drawable.pause)
            }
        }

        fun String?.formatDate(): String? = try {
            this?.let {
                val parsedDate = inputFormat.parse(this)
                outputFormat.format(parsedDate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        tvReleaseDate.text = releaseDate.formatDate()
        tvCountry.text = country
        tvTrackTimeMillis.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
        tvPrimaryGenreName.text = primaryGenreName
        tvCollectionName.text = collectionName
        tvTrackName.text = trackName
        tvArtistName.text = artistName



        Glide.with(this)
            .load(imageUrl)
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(30))
            .into(artworkUrl100)

        val imageButtonBack = findViewById<ImageButton>(R.id.button_back)
        imageButtonBack.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (playerState == STATE_PLAYING) {
            mediaPlayer.start()
            playButton.setImageResource(R.drawable.pause)
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
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playButton.setImageResource(R.drawable.play)
            mainThreadHandler?.removeCallbacks(progressRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
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

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        mainThreadHandler?.postDelayed(progressRunnable, DELAY)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private val progressRunnable = object : Runnable {
        override fun run() {
                val currentPosition = mediaPlayer.currentPosition
            playbackProgress.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
            mainThreadHandler?.postDelayed(this, DELAY)
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        const val DELAY = 300L
    }
}