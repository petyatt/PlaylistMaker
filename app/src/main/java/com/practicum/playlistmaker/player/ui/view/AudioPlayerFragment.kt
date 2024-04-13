package com.practicum.playlistmaker.player.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioplayerBinding
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigation: BottomNavigationView
    private val viewModel by viewModel<AudioPlayerViewModel>()
    private val addTrackToPlaylistAdapter: AddTrackToPlaylistAdapter by lazy {
        AddTrackToPlaylistAdapter(
            playlists = emptyList(),
            tracks =  arguments?.getSerializable("track") as Track) { playlist, track ->
            viewModel.addTrackToPlaylist(track, playlist)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioplayerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val track = arguments?.getSerializable("track") as Track

        bottomNavigation = requireActivity().findViewById(R.id.bottom_Navigation)
        bottomNavigation.visibility = View.GONE

        binding.recyclerViewAddTrackToPlaylist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewAddTrackToPlaylist.adapter = addTrackToPlaylistAdapter

        val bottomSheetContainer = binding.standardBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.bottomSheetState.observe(viewLifecycleOwner) { state ->
            bottomSheetBehavior.state = state
        }

        binding.playListButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.isVisible = false
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                 binding.overlay.alpha = slideOffset
            }
        })

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_newPlaylistFragment)
        }

        viewModel.playlistsLiveData.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNotEmpty()) {
                addTrackToPlaylistAdapter.setPlaylists(playlists)
                addTrackToPlaylistAdapter.notifyDataSetChanged()
            }
        }

        viewModel.getAllPlaylists()

        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is PlayerState.Playing -> {
                    binding.playButton.setImageResource(R.drawable.pause)
                }
                is PlayerState.Prepared, is PlayerState.Paused -> {
                    binding.playButton.setImageResource(R.drawable.play)
                }
                else -> { }
            }
            binding.playbackProgress.text = state.progress
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) {isFavorite ->
            if (isFavorite) {
                binding.favoritebutton.setImageResource(R.drawable.favorite)
            } else {
                binding.favoritebutton.setImageResource(R.drawable.favorite_border_l)
            }
        }

        viewModel.observeFavourite(track)

        viewModel.preparePlayer(track.previewUrl)

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.favoritebutton.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        binding.tvYear.text = track.releaseDate.substring(0, 4)
        binding.tvCountry.text = track.country
        binding.tvDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        binding.tvGenre.text = track.primaryGenreName
        binding.tvAlbum.text = track.collectionName
        binding.textView14.text = track.trackName
        binding.textView15.text = track.artistName

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(30))
            .into(binding.imageView6)

        viewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }


    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
        binding.playButton.setImageResource(R.drawable.play)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
        _binding = null
    }
}
