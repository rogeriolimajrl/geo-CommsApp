package com.rogerio.geocomms.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rogerio.geocomms.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.*

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val composingText: String = ""
)

class ChatViewModel(
    private val repo: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = repo.messages.stateIn(viewModelScope, SharingStarted.Lazily, emptyList()).let { flow ->
        // map repo messages to ui state on demand
        MutableStateFlow(ChatUiState(messages = flow.value))
    }

    // Simpler: expose messages directly
    val messages = repo.messages

    fun updateComposing(text: String) {
        _uiState.value = _uiState.value.copy(composingText = text)
    }

    fun sendText(text: String) {
        if (text.isBlank()) return
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = Sender.USER,
            text = text
        )
        repo.sendMessage(msg)
        // Simulate a reply
        repo.simulateExternalTextReply(replyText = "Resposta: $text")
    }

    fun sendAudio(localPath: String) {
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = Sender.USER,
            audioPath = localPath
        )
        repo.sendMessage(msg)
        repo.simulateExternalAudioReply(audioPath = "android.resource://com.rogerio.geocomms/raw/sample_reply")
    }
}
