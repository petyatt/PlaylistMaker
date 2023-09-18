package com.practicum.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.presentation.ui.switchTheme.App
import com.practicum.playlistmaker.presentation.ui.main.MainActivity
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = (application as App).darkTheme
        themeSwitcher.setOnCheckedChangeListener { _ , Checked ->
            (application as App).switchTheme(Checked)
        }

        val buttonBack = findViewById<Button>(R.id.button_back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val shareApp = findViewById<Button>(R.id.button_share_app)
        shareApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = getString(R.string.text_plain)
            val  appUrl = getString(R.string.https_yandex_ru_practicum)
            intent.putExtra(Intent.EXTRA_TEXT, appUrl)
            startActivity(intent)
        }

        val writeSupport = findViewById<Button>(R.id.button_write_support)
        writeSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse(getString(R.string.mailto_petya_07_yandex_ru))
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.playlist_maker_message_developers)
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.thanks_developers)
            )
            startActivity(intent)
        }

        val userAgreement = findViewById<Button>(R.id.button_user_agreement)
        userAgreement.setOnClickListener {
            val agreementUrl = getString(R.string.https_yandex_ru_legal_practicum_offer)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
            startActivity(intent)
        }
    }
}