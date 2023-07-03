package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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

    private val iTunesSearchAPIBaseUrl = "https://itunes.apple.com"

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

        val queryInput = findViewById<EditText>(R.id.inputEditText)
        val tracksList = findViewById<RecyclerView>(R.id.recyclerView)
        val placeholderView = findViewById<ImageView>(R.id.placeholderView)
        val placeholderText = findViewById<TextView>(R.id.placeholderText)
        val refrechButton = findViewById<Button>(R.id.refrech_button)

        adapter.tracks = tracks

        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksList.adapter = adapter

        fun performSearch() {
            val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            iTunesService.search(queryInput.text.toString())
                .enqueue(object : Callback<TrackResponse> {

                    override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                        if (response.code() == 200) {
                            tracksList.visibility = View.VISIBLE
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                            }

                            runOnUiThread {
                                adapter.notifyDataSetChanged()

                                if (tracks.isEmpty()) {
                                    placeholderText.setText(R.string.error_not_found)
                                    if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                                        placeholderView.setImageResource(R.drawable.dark_mode_no_search)
                                    } else {
                                        placeholderView.setImageResource(R.drawable.light_mode_no_search)
                                    }
                                    placeholderView.visibility = View.VISIBLE
                                    placeholderText.visibility = View.VISIBLE
                                    refrechButton.visibility = GONE
                                } else {
                                    placeholderView.visibility = GONE
                                    placeholderText.visibility = GONE
                                    refrechButton.visibility = GONE
                                }
                            }
                        } else {
                            runOnUiThread {
                                tracksList.visibility = GONE
                                tracks.clear()
                                adapter.notifyDataSetChanged()
                                placeholderText.setText(R.string.error_not_internet)
                                if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                                    placeholderView.setImageResource(R.drawable.dark_mode_no_internet)
                                } else {
                                    placeholderView.setImageResource(R.drawable.light_mode_no_internet)
                                }
                                placeholderView.visibility = View.VISIBLE
                                placeholderText.visibility = View.VISIBLE
                                refrechButton.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        runOnUiThread {
                            placeholderText.setText(R.string.error_not_internet)
                            if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                                placeholderView.setImageResource(R.drawable.dark_mode_no_internet)
                            } else {
                                placeholderView.setImageResource(R.drawable.light_mode_no_internet)
                            }
                            placeholderView.visibility = View.VISIBLE
                            placeholderText.visibility = View.VISIBLE
                            refrechButton.visibility = View.VISIBLE
                            tracksList.visibility = GONE
                        }
                    }

                })
        }

        refrechButton.setOnClickListener {
            placeholderView.visibility = GONE
            placeholderText.visibility = GONE
            refrechButton.visibility = GONE
            performSearch()
        }

        queryInput.setOnEditorActionListener { v, actionId, event ->
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
}