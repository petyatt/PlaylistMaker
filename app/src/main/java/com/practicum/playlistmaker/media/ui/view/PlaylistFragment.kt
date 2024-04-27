package com.practicum.playlistmaker.media.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment: Fragment() {

    private val viewModel by viewModel<PlaylistViewModel>()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val playlistAdapter: PlaylistAdapter by lazy {
        PlaylistAdapter { playlist ->
            val bundle = Bundle()
            bundle.putLong("playlistId", playlist.playlistId)
            findNavController().navigate(R.id.action_mediaFragment_to_playlistInfoFragment, bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecyclerView.adapter = playlistAdapter

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }

        viewModel.playlistsLiveData.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNotEmpty()) {
                playlistAdapter.setPlaylists(playlists)
                binding.placeholderView.isVisible = false
                binding.placeholderViewPlaylistNotFound.isVisible = false
            } else {
                binding.placeholderView.isVisible = true
                binding.placeholderViewPlaylistNotFound.isVisible = true
            }
        }

        viewModel.getAllPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun newInstance() = PlaylistFragment()
    }
}