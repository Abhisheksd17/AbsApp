package com.example.feature_chat.utils

import android.media.MediaRecorder
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.io.File

data class VoiceRecorderState(
    val isRecording: Boolean = false,
    val elapsedSeconds: Int = 0,
    val isCancelled: Boolean = false,
    val recordingFile: File? = null,
)

@Composable
fun rememberVoiceRecorder(
    onRecordingComplete: (File) -> Unit = {},
    onRecordingCancelled: () -> Unit = {},
): Triple<VoiceRecorderState, () -> Unit, () -> Unit> {

    val context = LocalContext.current
    var state by remember { mutableStateOf(VoiceRecorderState()) }

    // Use Ref so pointerInput lambda captures a stable reference that
    // never changes across recompositions — this is the key fix for
    // the "recording cancels itself" bug.
    val recorderRef   = remember { mutableStateOf<MediaRecorder?>(null) }
    val outputFileRef = remember { mutableStateOf<File?>(null) }
    val cancelledRef  = remember { mutableStateOf(false) }

    // Callbacks also need to be stable refs so pointerInput doesn't restart
    val onCompleteRef   = rememberUpdatedState(onRecordingComplete)
    val onCancelledRef  = rememberUpdatedState(onRecordingCancelled)

    /* Timer — keyed on isRecording flag */
    LaunchedEffect(state.isRecording) {
        if (state.isRecording) {
            var seconds = 0
            while (true) {
                kotlinx.coroutines.delay(1_000)
                if (!state.isRecording) break
                seconds++
                state = state.copy(elapsedSeconds = seconds)
            }
        }
    }

    val startRecording: () -> Unit = {
        cancelledRef.value = false
        val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
        outputFileRef.value = file

        val mr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        mr.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128_000)
            setAudioSamplingRate(44_100)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        recorderRef.value = mr
        state = VoiceRecorderState(isRecording = true, elapsedSeconds = 0, recordingFile = file)
    }

    val stopRecording: () -> Unit = {
        recorderRef.value?.runCatching { stop(); release() }
        recorderRef.value = null

        val file      = outputFileRef.value
        val wasCancelled = cancelledRef.value

        if (!wasCancelled && file != null && file.exists() && file.length() > 0) {
            state = state.copy(isRecording = false)
            onCompleteRef.value(file)
        } else {
            outputFileRef.value?.delete()
            state = VoiceRecorderState(isRecording = false, isCancelled = wasCancelled)
            if (wasCancelled) onCancelledRef.value()
        }
        outputFileRef.value  = null
        cancelledRef.value   = false
    }

    val cancelRecording: () -> Unit = {
        cancelledRef.value = true
        stopRecording()
    }

    return Triple(state, startRecording, stopRecording)
}

fun formatRecordingTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "$m:${s.toString().padStart(2, '0')}"
}