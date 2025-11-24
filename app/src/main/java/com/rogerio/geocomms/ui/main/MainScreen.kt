package com.rogerio.geocomms.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rogerio.geocomms.MainViewModel
import com.rogerio.geocomms.chat.ChatPanel
import com.rogerio.geocomms.ui.map.MapPanel

@Composable
fun MainScreen(mainViewModel: MainViewModel) {

    val mapVm = mainViewModel.mapViewModel
    val chatVm = mainViewModel.chatViewModel

    // state to minimize panels
    var mapMinimized by remember { mutableStateOf(false) }
    var chatMinimized by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Map Column
        Column(
            modifier = Modifier
                .weight(if (mapMinimized) 0.05f else if (chatMinimized) 0.95f else 0.65f)
                .fillMaxHeight()
                .padding(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Map", modifier = Modifier.weight(1f))
                IconButton(onClick = { mapMinimized = !mapMinimized }) {
                    if(mapMinimized)
                        Icon(imageVector = Icons.Default.ArrowCircleRight, contentDescription = "")
                    else
                        Icon(imageVector = Icons.Default.ArrowCircleLeft, contentDescription = "")

                }
            }
            if (!mapMinimized) {
                MapPanel(mapVm, { mainViewModel.startVoiceRecognition() })
            }
        }

        // Chat Column
        Column(
            modifier = Modifier
                .weight(if (chatMinimized) 0.05f else if (mapMinimized) 0.95f else 0.35f)
                .fillMaxHeight()
                .padding(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Chat", modifier = Modifier.weight(1f))
                IconButton(onClick = { chatMinimized = !chatMinimized }) {
                    if(chatMinimized)
                        Icon(imageVector = Icons.Default.ArrowCircleLeft, contentDescription = "")
                    else
                        Icon(imageVector = Icons.Default.ArrowCircleRight, contentDescription = "")
                }
            }
            if (!chatMinimized) {
                ChatPanel(chatVm)
            }
        }
    }

}
