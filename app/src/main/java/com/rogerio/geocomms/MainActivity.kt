package com.rogerio.geocomms

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.mapbox.common.MapboxOptions
import com.rogerio.geocomms.ui.main.MainScreen
import com.rogerio.geocomms.ui.theme.GeoCommsTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    // Permissions launcher
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->

        }

    // Voice recognition launcher (startActivityForResult style)
    private val voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val matches = intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognized = matches?.firstOrNull() ?: ""
            if (recognized.isNotBlank()) {
                // pass result into ViewModel
                mainViewModel.onVoiceRecognized(recognized)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = getString(R.string.mapbox_access_token)
        MapboxOptions.accessToken = token

        // request microphone permission and storage/internet optionally
        requestPermissions.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            GeoCommsTheme {
                Surface(color = MaterialTheme.colors.background) {
                    // Pass the voiceLauncher starter to the MainScreen through the activity's ViewModel
                    mainViewModel.setVoiceStarter { startIntent ->
                        // startIntent is an Intent to RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                        voiceLauncher.launch(startIntent)
                    }
                    MainScreen(mainViewModel)
                }
            }
        }
    }
}
