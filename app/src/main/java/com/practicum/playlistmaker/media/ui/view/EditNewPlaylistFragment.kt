package com.practicum.playlistmaker.media.ui.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.media.domain.model.Playlist
import com.practicum.playlistmaker.media.ui.view_model.EditNewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditNewPlaylistFragment : NewPlaylistFragment() {

    private var isImageSelected = false
    private val viewModel by viewModel<EditNewPlaylistViewModel>()
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

        setupUI()
        setupListeners()

        viewModel.selectedImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                loadImage(uri.toString())
            } ?: loadImage(null)
        }

        viewModel.isImageSelected.observe(viewLifecycleOwner) { isSelected ->
            isImageSelected = isSelected
            if (!isSelected) {
                Glide.with(this).load(R.drawable.placeholder).into(binding.imageViewRectangel)
            }
        }

        binding.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.setImageUri(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
            viewModel.setImageUri(null)
        }
    }

    private fun setupListeners() {
        binding.imageButtonCreate.setOnClickListener {
            handleSave()
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageViewRectangel.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun updateSaveButtonState() {
        val isTitleNotEmpty = binding.editTextTitle.text.toString().trim().isNotEmpty()
        binding.imageButtonCreate.isEnabled = isTitleNotEmpty
        binding.imageButtonCreate.setImageResource(
            if (isTitleNotEmpty) R.drawable.button_create_action else R.drawable.button_create
        )
    }

    private fun loadImage(imagePath: String?) {
        Glide.with(requireContext())
            .load(imagePath)
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(10)))
            .into(binding.imageViewRectangel)
    }

    override fun setupUI() {
        binding.textNewPlaylist.text = "Редактировать плейлист"
        binding.imageButtonCreate.setImageResource(R.drawable.button_create)
        binding.textViewCreate.text = "Сохранить"
        val playlist: Playlist? = arguments?.getParcelable("playlist")
        playlist?.let {
            binding.editTextTitle.setText(it.title)
            binding.editTextDescription.setText(it.description)
            loadImage(it.imagePath)
        }

        updateSaveButtonState()
    }

    override fun handleSave() {
        val playlist: Playlist? = arguments?.getParcelable("playlist")
        val title = binding.editTextTitle.text.toString().trim()
        if (title.isNotEmpty()) {
            val description = binding.editTextDescription.text.toString().trim()
            val imagePath = viewModel.selectedImageUri.value?.let { uri -> viewModel.saveImageToPrivateStorage(uri) } ?: playlist?.imagePath ?: ""
            val updatedPlaylist = playlist?.copy(title = title, description = description, imagePath = imagePath)
            updatedPlaylist?.let {
                viewModel.updatePlaylist(it)
                Toast.makeText(requireContext(), "Плейлист \"${it.title}\" обновлён", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}