package com.practicum.playlistmaker.media.ui.view

import android.content.Intent
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
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.practicum.playlistmaker.media.domain.model.Playlist
import com.practicum.playlistmaker.media.ui.view_model.PlaylistInfoViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

class PlaylistInfoFragment: Fragment() {

    private val viewModel by viewModel<PlaylistInfoViewModel>()
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!
    private var playlistId by Delegates.notNull<Long>()
    private var currentPlaylist: Playlist? = null

    private val tracklistAdapter = TrackAdapter(object : TrackAdapter.OnClickListener {
        override fun onTrackClick(track: Track) {
            val args = Bundle()
            args.putSerializable("track", track)
            findNavController().navigate(
                R.id.action_playlistInfoFragment_to_audioPlayerFragment,
                args
            )
        }

        override fun onTrackLongClick(track: Track) {
            showDeleteTrackDialog(track)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPlaylistInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        setupListeners()

        currentPlaylist = arguments?.getParcelable("playlist")
        playlistId = arguments?.getLong("playlistId") ?: return

        viewModel.playlistLiveData.observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                updateUI(playlist)
                updatePlaylistDetails(playlist)
                currentPlaylist = playlist
            }
        }

        binding.recyclerViewBottomSheet.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewBottomSheet.adapter = tracklistAdapter

        viewModel.playlistDeleted.observe(viewLifecycleOwner) {deleted ->
            if(deleted) {
                findNavController().popBackStack()
            }
        }

        viewModel.playlistLiveData.observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                updateUI(playlist)
                updatePlaylistDetails(playlist)
            }
        }

        viewModel.tracksLiveData.observe(viewLifecycleOwner) { tracks ->
            tracklistAdapter.tracks = ArrayList(tracks)
            tracklistAdapter.notifyDataSetChanged()

            binding.tvEmptyPlaylist.isVisible = tracks.isEmpty()
            if (tracks.isEmpty()) {
                binding.tvEmptyPlaylist.text = "В этом плейлисте нет треков"
            }
        }

        viewModel.getPlaylist(playlistId)
    }

    private fun setupBottomSheet() {
        val bottomSheetContainer = binding.bottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        val secondBottomSheetContainer = binding.secondBottomSheet
        val  secondBottomSheetBehavior = BottomSheetBehavior.from(secondBottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        binding.overlay.isVisible = false
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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

        secondBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        binding.overlay.isVisible = false
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
    }

    private fun shareTracks() {
        val tracks = viewModel.tracksLiveData.value ?: emptyList()
        if (tracks.isEmpty()) {
            Toast.makeText(context, "В этом плейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_SHORT).show()
        } else {
            sharePlaylist(tracks)
        }
    }

    private fun setupListeners() {
        val secondBottomSheetContainer = binding.secondBottomSheet
        val  secondBottomSheetBehavior = BottomSheetBehavior.from(secondBottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.tvEditInfo.setOnClickListener {
            currentPlaylist?.let { playlist ->
                val args = Bundle()
                args.putParcelable("playlist", playlist)
                findNavController().navigate(
                    R.id.action_playlistInfoFragment_to_editNewPlaylistFragment,
                    args
                )
            }
        }

        binding.tvDeletePlaylist.setOnClickListener {
            showDeletePlaylistDialog()
            secondBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.ivShare.setOnClickListener {
            shareTracks()
        }

        binding.ivShareMenu.setOnClickListener {
            shareTracks()
        }

        binding.ivMenu.setOnClickListener {
            secondBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun sharePlaylist(tracks: List<Track>) {
        val playlist = viewModel.playlistLiveData.value ?: return
        val shareText = buildString {
            append(playlist.title).append("\n")
            append(playlist.description).append("\n")
            append(viewModel.getTracksWord(playlist.trackCount, "tracks")).append("\n\n")
            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName} (${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)})\n")
            }
        }

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun updateUI(playlist: Playlist) {
        binding.tvTitle.text = playlist.title
        binding.tvDescription.text = playlist.description
        binding.tvTrackCount.text = viewModel.getTracksWord(playlist.trackCount, "tracks")

        viewModel.totalMinutesLiveData.observe(viewLifecycleOwner) { totalMinutes ->
            binding.tvCountTime.text = viewModel.getTracksWord(totalMinutes, "minutes")
        }

        Glide.with(requireContext())
            .load(playlist.imagePath)
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(10)))
            .into(binding.ivCover)
    }

    private fun updatePlaylistDetails(playlist: Playlist?) {
        playlist?.let {
            Glide.with(requireContext())
                .load(playlist.imagePath)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(10)))
                .into(binding.ivPlaylistCover)

            binding.tvPlaylistName.text = playlist.title
            binding.tvSecondTrackCount.text = viewModel.getTracksWord(playlist.trackCount, "tracks")
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить трек")
            .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
            .setPositiveButton("Удалить") { dialog, which ->
                viewModel.deleteTrackFromPlaylist(playlistId, track.trackId)
                tracklistAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист?")
            .setNegativeButton("Нет", null)
            .setPositiveButton("Да") { _, _ ->
                    viewModel.deletePlaylist(playlistId)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewBottomSheet.adapter = null
        _binding = null
    }
}