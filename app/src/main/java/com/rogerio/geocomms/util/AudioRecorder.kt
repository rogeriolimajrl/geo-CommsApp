package com.rogerio.geocomms.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import java.io.File

class AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var outputPath: String? = null
    private val handler = Handler(Looper.getMainLooper())

    fun startRecording(context: Context, maxMs: Long = 30_000L): String {
        val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
        outputPath = file.absolutePath
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputPath)
            prepare()
            start()
        }

        // schedule auto-stop after maxMs
        handler.postDelayed({ stopRecording() }, maxMs)
        return outputPath!!
    }

    fun stopRecording(): String? {
        return try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            outputPath
        } catch (e: Exception) {
            recorder = null
            outputPath
        }
    }

    fun cancel() {
        try {
            recorder?.stop()
        } catch (ignored: Exception) {}
        recorder?.release()
        recorder = null
        outputPath = null
    }
}
