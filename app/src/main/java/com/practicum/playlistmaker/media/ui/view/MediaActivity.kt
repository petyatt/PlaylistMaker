package com.practicum.playlistmaker.media.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)

        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> resources.getString(R.string.favorites_tracks)
                1 -> resources.getString(R.string.playlists)
                else -> "tab"
            }
        }

        tabLayoutMediator.attach()

        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}