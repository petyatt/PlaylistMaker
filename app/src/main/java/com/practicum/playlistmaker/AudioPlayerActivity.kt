package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private var isAppInBackground = false
    private lateinit var searchHistory: SearchHistory
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App
        app.switchTheme(app.darkTheme)

        setContentView(R.layout.activity_player)

        sharedPreferences = getSharedPreferences(SearchActivity.SAVE_HISTORY, Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

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

        fun String?.formatDate(): String? = try {
            this?.let {
                val parsedDate = inputFormat.parse(this)
                outputFormat.format(parsedDate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        val formattedDate = releaseDate.formatDate()
        val playButton = findViewById<ImageButton>(R.id.playButton)
        val artworkUrl100 = findViewById<ImageView>(R.id.imageView6)
        val tvCollectionName = findViewById<TextView>(R.id.tvAlbum)
        val tvPrimaryGenreName = findViewById<TextView>(R.id.tvGenre)
        val tvCountry = findViewById<TextView>(R.id.tvCountry)
        val tvReleaseDate = findViewById<TextView>(R.id.tvYear)
        val tvTrackTimeMillis = findViewById<TextView>(R.id.tvDuration)
        val tvTrackName = findViewById<TextView>(R.id.textView14)
        val tvArtistName = findViewById<TextView>(R.id.textView15)

        val backgroundColor = if (app.darkTheme) R.drawable.round_button_dark else R.drawable.round_button
        playButton.setBackgroundResource(backgroundColor)

        tvReleaseDate.text = formattedDate
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

    override fun onPause() {
        super.onPause()
        isAppInBackground = true
    }

    override fun onResume() {
        super.onResume()

        if (isAppInBackground) {

            val intent = Intent(this, AudioPlayerActivity::class.java)
            startActivity(intent)
            finish()
        }

        isAppInBackground = false
    }
}