package com.practicum.playlistmaker.media.ui.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.media.domain.model.Playlist
import com.practicum.playlistmaker.media.ui.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment: Fragment() {

    private var selectedImageUri: Uri? = null
    private var isImageSelected = false
    private val viewModel by viewModel<NewPlaylistViewModel>()
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() =_binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.buttonBack.setOnClickListener {
            showDialog()
        }

        binding.imageViewRectangel.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!binding.editTextTitle.text.isNullOrEmpty() ||
                    !binding.editTextDescription.text.isNullOrEmpty() ||
                    isImageSelected) {
                    showDialog()
                } else {
                    findNavController().popBackStack()
                }
            }
        })

        binding.editTextTitle.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrBlank()) {
                    binding.imageButtonCreate.setImageResource(R.drawable.button_create)
                } else {
                    binding.imageButtonCreate.setImageResource(R.drawable.button_create_action)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.imageButtonCreate.setOnClickListener {
            binding.imageButtonCreate.setOnClickListener {
                val title = binding.editTextTitle.text.toString().trim()
                if (title.isNotEmpty()) {
                    val description = binding.editTextDescription.text.toString().trim()
                    val imagePath =
                        selectedImageUri?.let { uri -> saveImageToPrivateStorage(uri) } ?: ""
                    val playlist = Playlist(
                        title = title,
                        description = description,
                        imagePath = imagePath,
                        trackCount = 0,
                        trackList = emptyList()
                    )
                    viewModel.createPlaylist(playlist)
                    showSuccessMessage(playlist.title)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun showSuccessMessage(playlistName: String) {
        Toast.makeText(requireContext(), "Плейлист $playlistName успешно создан", Toast.LENGTH_SHORT).show()
    }

    private fun showDialog() {
        if (!binding.editTextTitle.text.isNullOrEmpty() ||
            !binding.editTextDescription.text.isNullOrEmpty() ||
            isImageSelected) {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
                .setNegativeButton("Отмена") { dialog, which ->
                }
                .setPositiveButton("Да") { dialog, which ->
                    findNavController().popBackStack()
                }
            dialog.show()
        } else {
            findNavController().popBackStack()
        }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                Glide.with(requireActivity())
                    .load(uri)
                    .into(binding.imageViewRectangel)
                isImageSelected = true
                saveImageToPrivateStorage(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
                selectedImageUri = null
            }
        }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        return viewModel.saveImageToPrivateStorage(uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}