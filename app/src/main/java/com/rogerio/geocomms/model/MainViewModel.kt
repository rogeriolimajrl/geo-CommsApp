package com.rogerio.geocomms

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogerio.geocomms.model.ChatViewModel
import com.rogerio.geocomms.repository.ChatRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val chatRepository = ChatRepository()

    val mapViewModel = MapViewModel()
    val chatViewModel = ChatViewModel(chatRepository)

    // function provided by Activity to actually launch the voice Intent
    private var voiceStarter: ((Intent) -> Unit)? = null

    fun setVoiceStarter(starter: (Intent) -> Unit) {
        voiceStarter = starter
    }

    // Called by UI to start voice recognition
    fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale agora")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        voiceStarter?.invoke(intent)
    }

    // Called by Activity with recognized text
    fun onVoiceRecognized(text: String) {
        // pass to map view model to perform search & add markers
        viewModelScope.launch {
            // Simulate
            //mapViewModel.onVoiceResult(text)
            // Real
            mapViewModel.searchRealPlaces(text)
        }
    }
}
