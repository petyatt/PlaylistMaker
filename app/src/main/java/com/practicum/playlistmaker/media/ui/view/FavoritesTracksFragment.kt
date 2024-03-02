package com.practicum.playlistmaker.media.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.practicum.playlistmaker.media.domain.model.MediaTrackState
import com.practicum.playlistmaker.media.ui.view_model.FavoritesTracksViewModel
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment: Fragment() {

    private var _binding: FragmentFavoritesTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoritesTracksViewModel>()

    private val adapter = TrackAdapter { track ->
        if (viewModel.clickDebounce()) {
            saveTrackAndStartActivity(track)
        }
    }

    private fun saveTrackAndStartActivity(track: Track) {
        viewModel.saveTrack(track)
        viewModel.getFavoriteTrack()
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(TRACK, track)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.recyclerViewFavoriteTrack.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewFavoriteTrack.adapter = adapter

        viewModel.observeState().observe(viewLifecycleOwner) {state ->
            when(state) {
                is MediaTrackState.Content -> showContent(state.data)
                is MediaTrackState.Empty -> showEmpty()
            }
        }
        viewModel.getFavoriteTrack()
    }

    private fun showEmpty() {
        binding.recyclerViewFavoriteTrack.isVisible = false
        binding.imageViewPlaceholderView.isVisible = true
        binding.placeholderViewMediaNotFound.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        binding.recyclerViewFavoriteTrack.isVisible = true
        binding.imageViewPlaceholderView.isVisible = false
        binding.placeholderViewMediaNotFound.isVisible = false

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteTrack()
    }

    companion object{
        fun newInstance() = FavoritesTracksFragment()
        const val TRACK = "track"
    }
}