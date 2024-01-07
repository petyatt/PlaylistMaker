package com.practicum.playlistmaker.settings.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.application.App
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding

class SettingsFragment: Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        super.onCreate(savedInstanceState)

        val appInstance = requireActivity().application as? App
        binding.themeSwitcher.isChecked = appInstance?.darkTheme ?: false
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            appInstance?.switchTheme(checked)
        }

        binding.buttonShareApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = getString(R.string.text_plain)
            val  appUrl = getString(R.string.https_yandex_ru_practicum)
            intent.putExtra(Intent.EXTRA_TEXT, appUrl)
            startActivity(intent)
        }

        binding.buttonWriteSupport.setOnClickListener {
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

        binding.buttonUserAgreement.setOnClickListener {
            val agreementUrl = getString(R.string.https_yandex_ru_legal_practicum_offer)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
            startActivity(intent)
        }
        return  binding.root
    }

//    companion object{
//        fun newInstance() = SettingsFragment()
//    }
}