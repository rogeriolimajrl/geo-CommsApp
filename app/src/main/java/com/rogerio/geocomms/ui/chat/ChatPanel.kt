package com.rogerio.geocomms.chat


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rogerio.geocomms.model.ChatMessage
import com.rogerio.geocomms.model.ChatViewModel
import com.rogerio.geocomms.model.Sender
import com.rogerio.geocomms.util.AudioPlayer
import com.rogerio.geocomms.util.AudioRecorder


@Composable
fun ChatPanel(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Recorder & Player
    val recorder = remember { AudioRecorder() }
    val player = remember { AudioPlayer() }

    var composing by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var recordedPath by remember { mutableStateOf<String?>(null) }
    var playingId by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Text(
            text = "Chat",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(12.dp)
        )

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false,
            contentPadding = PaddingValues(all = 8.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(
                    message = msg,
                    onPlay = { m ->
                        val audio = m.audioPath
                        if (!audio.isNullOrBlank()) {
                            try {
                                player.play(path = audio) {
                                    playingId = null
                                }
                                playingId = m.id
                            } catch (e: Exception) {
                                Log.e("ChatPanel", "play failed: ${e.message}")
                            }
                        }
                    }
                )
            }
        }


        // Divider
        Divider()

        // Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = composing,
                onValueChange = { composing = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escreva uma mensagem...") }
            )

            IconButton(onClick = {
                if (composing.isNotBlank()) {
                    viewModel.sendText(composing)
                    composing = ""
                }
            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar")
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Record button: toggle recording
            IconButton(onClick = {
                if (!isRecording) {
                    // start recording
                    try {
                        val path = recorder.startRecording(context)
                        recordedPath = path
                        isRecording = true
                        // optionally show toast
                    } catch (e: Exception) {
                        Log.e("ChatPanel", "start rec err: ${e.message}")
                    }
                } else {
                    // stop and send
                    val path = recorder.stopRecording()
                    isRecording = false
                    recordedPath = path
                    if (!path.isNullOrBlank()) {
                        viewModel.sendAudio(path)
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = if (!isRecording) "Gravar áudio" else "Parar gravação"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MessageBubble(message: ChatMessage, onPlay: (ChatMessage) -> Unit) {
    val isUser = message.sender == Sender.USER
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            message.text?.let {
                Text(text = it, color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            }

            if (!message.audioPath.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .padding(top = 6.dp)
                    .clickable { onPlay(message) }) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Tocar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Mensagem de voz", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
