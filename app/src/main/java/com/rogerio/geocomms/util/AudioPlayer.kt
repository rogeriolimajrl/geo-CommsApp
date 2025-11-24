package com.rogerio.geocomms.util

import android.media.MediaPlayer

class AudioPlayer {
    private var player: MediaPlayer? = null

    fun play(path: String, onComplete: (() -> Unit)? = null) {
        stop()
        player = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
            setOnCompletionListener {
                onComplete?.invoke()
            }
        }
    }

    fun stop() {
        try {
            player?.stop()
            player?.release()
        } catch (ignored: Exception) {}
        player = null
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }
}
