package com.practicum.playlistmaker.presentation.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.ui.media.MediaActivity
import com.practicum.playlistmaker.presentation.ui.settings.SettingsActivity
import com.practicum.playlistmaker.presentation.ui.track.SearchTrackActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonMedia = findViewById<Button>(R.id.button_media)
        buttonMedia.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val buttonSearch = findViewById<Button>(R.id.button_search)
        buttonSearch.setOnClickListener {
            val searchIntent = Intent(this, SearchTrackActivity::class.java)
            startActivity(searchIntent)
        }

        val buttonSettings = findViewById<Button>(R.id.button_settings)
        buttonSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}