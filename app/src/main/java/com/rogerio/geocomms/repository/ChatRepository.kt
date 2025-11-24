package com.rogerio.geocomms.repository

import com.rogerio.geocomms.model.ChatMessage
import com.rogerio.geocomms.model.Sender
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatRepository {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun sendMessage(msg: ChatMessage) {
        _messages.value = _messages.value + msg
    }

    fun simulateExternalTextReply(afterMs: Long = 1200L, replyText: String = "Entendido!") {
        GlobalScope.launch {
            delay(afterMs)
            _messages.value = _messages.value + ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = Sender.EXTERNAL,
                text = replyText
            )
        }
    }

    fun simulateExternalAudioReply(afterMs: Long = 1500L, audioPath: String) {
        GlobalScope.launch {
            delay(afterMs)
            _messages.value = _messages.value + ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = Sender.EXTERNAL,
                audioPath = audioPath
            )
        }
    }
}
