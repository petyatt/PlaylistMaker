package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var queryInput: EditText
    private lateinit var tracksList: RecyclerView
    private lateinit var placeholderView: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var refreshButton: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesSearchAPIBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ItunesApiService::class.java)

    private var tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        queryInput = findViewById(R.id.inputEditText)
        tracksList = findViewById(R.id.recyclerView)
        placeholderView = findViewById(R.id.placeholderView)
        placeholderText = findViewById(R.id.placeholderText)
        refreshButton = findViewById(R.id.refrech_button)

        adapter.tracks = tracks

        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksList.adapter = adapter

        fun performSearch() {
            iTunesService.search(queryInput.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                        handleSearchResponse(response.code(), response.body()?.results)
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        runOnUiThread {
                            handleSearchFailure()
                        }
                    }
                })
        }

        refreshButton.setOnClickListener {
            placeholderView.visibility = GONE
            placeholderText.visibility = GONE
            refreshButton.visibility = GONE
            performSearch()
        }

        queryInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        if (savedInstanceState != null) {
            countValue = savedInstanceState.getInt(PRODUCT_AMOUNT, 0)
        }

        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        val buttonBack = findViewById<Button>(R.id.button_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val simpleTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        queryInput.addTextChangedListener(simpleTextWatcher)

        clearButton.setOnClickListener {
            queryInput.setText("")
            val hideKeyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideKeyboard.hideSoftInputFromWindow(queryInput.windowToken, 0)
            clearButton.visibility = GONE
            tracksList.visibility = GONE
            tracks.clear()
        }
    }

    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        const val iTunesSearchAPIBaseUrl = "https://itunes.apple.com"
    }

    private var countValue: Int = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PRODUCT_AMOUNT, countValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        countValue = savedInstanceState.getInt(PRODUCT_AMOUNT, 0)
    }

    fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            GONE
        } else {
            View.VISIBLE
        }
    }

    private fun handleSearchResponse(responseCode: Int, results: List<Track>?) {
        if (responseCode == 200) {
            tracksList.visibility = View.VISIBLE
            tracks.clear()
            if (results?.isNotEmpty() == true) {
                tracks.addAll(results)
            }
            adapter.notifyDataSetChanged()

            if (tracks.isEmpty()) {
                showEmptySearchResult()
            } else {
                hidePlaceholderView()
            }
        } else {
            handleSearchFailure()
        }
    }

    private fun handleSearchFailure() {
        tracksList.visibility = GONE
        tracks.clear()
        placeholderText.setText(R.string.error_not_internet)
        showPlaceholderView(R.drawable.no_internet)
        refreshButton.visibility = View.VISIBLE
    }

    private fun showEmptySearchResult() {
        placeholderText.setText(R.string.error_not_found)
        showPlaceholderView(R.drawable.no_search)
    }

    private fun showPlaceholderView(placeholderImageRes: Int) {
        placeholderView.setImageResource(placeholderImageRes)
        placeholderView.visibility = View.VISIBLE
        placeholderText.visibility = View.VISIBLE
        refreshButton.visibility = GONE
    }

    private fun hidePlaceholderView() {
        placeholderView.visibility = GONE
        placeholderText.visibility = GONE
        refreshButton.visibility = GONE
    }
}