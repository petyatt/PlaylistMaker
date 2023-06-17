package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import data.Tracks


class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TrackAdapter(Tracks.tracks)

        if (savedInstanceState != null) {
            countValue = savedInstanceState.getInt(PRODUCT_AMOUNT, 0)
        }

        val searchEditText = findViewById<EditText>(R.id.inputEditText)

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
        searchEditText.addTextChangedListener(simpleTextWatcher)

        clearButton.setOnClickListener {
            searchEditText.setText("")
            val hideKeyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideKeyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}