package com.rogerio.geocomms.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rogerio.geocomms.MapViewModel

@Composable
fun MapPanel(viewModel: MapViewModel, onVoiceSearch: () -> Unit) {

    Box(Modifier.fillMaxSize()) {

        AndroidView(
            factory = { ctx -> viewModel.createMapView(ctx) },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onVoiceSearch,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Busca por voz"
            )
        }
    }
}