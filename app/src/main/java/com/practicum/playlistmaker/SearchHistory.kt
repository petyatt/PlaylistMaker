package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

const val KEY_SEARCH_HISTORY = "key_search_history"

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun clearSearchHistory() {
        sharedPreferences.edit()
            .remove(KEY_SEARCH_HISTORY)
            .apply()
    }

    fun readSearchHistory(): MutableList<Track> {
        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null)
        val jsonG = GsonBuilder().create()
        return jsonG.fromJson(json, object: TypeToken<MutableList<Track>>() {} .type) ?: mutableListOf()
    }

    fun writeSearchHistory(track: Track) {
        val editor = sharedPreferences.edit()

        // Чтение текущей истории из SharedPreferences
        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null)
        val gson = Gson()
        val currentHistory: MutableList<Track> =
            gson.fromJson(json, object : TypeToken<MutableList<Track>>() {}.type) ?: mutableListOf()

        // Удаление предыдущей записи об этом треке из списка истории, если она уже есть
        currentHistory.removeAll { it.trackId == track.trackId }

        // Добавление нового выбранного трека в начало списка истории
        currentHistory.add(0, track)

        // Ограничение размера списка истории до 10 записей
        while (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        // Сохранение обновленной истории обратно в SharedPreferences
        val jsonHistory = gson.toJson(currentHistory)
        editor.putString(KEY_SEARCH_HISTORY, jsonHistory)
        editor.apply()
    }
}