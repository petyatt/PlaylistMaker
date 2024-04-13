package com.practicum.playlistmaker.media.ui.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewPlaylistViewModel(
    private val appContext: Context,
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val filePath by lazy {
        File(appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
                playlistInteractor.createNewPlaylist(playlist)
        }
    }

    fun saveImageToPrivateStorage(uri: Uri): String {
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = "${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)
        val inputStream = appContext.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.close()

        return file.absolutePath
    }
}