package com.example.feature_chat.ui

import android.Manifest
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature_chat.utils.formatRecordingTime
import com.example.feature_chat.utils.rememberMediaPicker
import com.example.feature_chat.utils.rememberVoiceRecorder
import com.example.feature_chat.utils.PickedMedia
import com.example.ui.theme.Black
import com.example.ui.theme.LightGrayBackground
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftLightGray
import com.example.ui.R as ui
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MessageInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit = {},
    onMediaPicked: (PickedMedia) -> Unit = {},
    onPhotoCaptured: (Uri) -> Unit = {},
    onVoiceRecorded: (File) -> Unit = {},
) {
    val (_, launchGallery, launchCamera) = rememberMediaPicker(
        onMediaPicked   = onMediaPicked,
        onPhotoCaptured = onPhotoCaptured,
    )
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val audioPermission  = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    val (recorderState, startRecording, stopRecording) = rememberVoiceRecorder(
        onRecordingComplete  = onVoiceRecorded,
        onRecordingCancelled = {},
    )

    val startRef  = rememberUpdatedState(startRecording)
    val stopRef   = rememberUpdatedState(stopRecording)
    val audioGrantedRef = rememberUpdatedState(audioPermission.status.isGranted)
    val requestAudioRef = rememberUpdatedState({ audioPermission.launchPermissionRequest() })

    val cancelThresholdPx = -200f


    val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue  = 1f,
        targetValue   = 1.22f,
        animationSpec = infiniteRepeatable(
            animation  = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )


    val dragOffsetX = remember { mutableStateOf(0f) }
    val isCancelMode = dragOffsetX.value < cancelThresholdPx

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBackground)
    ) {
        AnimatedVisibility(visible = recorderState.isRecording) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text  = formatRecordingTime(recorderState.elapsedSeconds),
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text(
                    text  = if (isCancelMode) "Release to cancel" else "< Slide to cancel",
                    color = if (isCancelMode) Color.Red else SlateGray,
                    fontSize = 13.sp,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter            = painterResource(ui.drawable.attach_ic),
                contentDescription = "Attach",
                modifier           = Modifier
                    .size(24.dp)
                    .clickable { launchGallery() },
                tint = Color.Unspecified
            )

            Spacer(Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Black, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftLightGray)
                        .padding(vertical = 14.dp, horizontal = 12.dp)
                        .alpha(if (recorderState.isRecording) 0f else 1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value         = message,
                        onValueChange = onMessageChange,
                        modifier      = Modifier.weight(1f),
                        textStyle     = TextStyle(fontSize = 14.sp, color = Black),
                        decorationBox = { inner ->
                            if (message.isEmpty()) {
                                Text("Write your message", color = SlateGray, fontSize = 12.sp)
                            }
                            inner()
                        }
                    )
                }

                if (recorderState.isRecording) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftLightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Recording…", color = SlateGray, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            if (message.isNotBlank() && !recorderState.isRecording) {
                Icon(
                    painter            = painterResource(ui.drawable.send_ic),
                    contentDescription = "Send",
                    modifier           = Modifier
                        .size(40.dp)
                        .clickable { onSend() },
                    tint = Color.Unspecified
                )
            } else {
                if (!recorderState.isRecording) {
                    Icon(
                        painter            = painterResource(ui.drawable.camera_ic),
                        contentDescription = "Camera",
                        modifier           = Modifier
                            .size(24.dp)
                            .clickable {
                                if (cameraPermission.status.isGranted) launchCamera()
                                else cameraPermission.launchPermissionRequest()
                            },
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(12.dp))
                }


                Icon(
                    painter            = painterResource(ui.drawable.vr_ic),
                    contentDescription = "Record voice",
                    tint               = if (recorderState.isRecording) Color.Red
                    else Color.Unspecified,
                    modifier           = Modifier
                        .size(24.dp)
                        .scale(if (recorderState.isRecording) pulseScale else 1f)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val down = awaitFirstDown(requireUnconsumed = false)

                                    if (!audioGrantedRef.value) {
                                        requestAudioRef.value()
                                        continue
                                    }

                                    dragOffsetX.value = 0f
                                    startRef.value()

                                    var startX = down.position.x

                                    loop@ while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Main)
                                        val ptr   = event.changes.firstOrNull() ?: break@loop

                                        dragOffsetX.value = ptr.position.x - startX

                                        if (!ptr.pressed) {
                                            if (dragOffsetX.value < cancelThresholdPx) {

                                                stopRef.value()
                                            } else {
                                                stopRef.value()
                                            }
                                            dragOffsetX.value = 0f
                                            break@loop
                                        }
                                        ptr.consume()
                                    }
                                }
                            }
                        }
                )
            }
        }
    }
}

private fun Modifier.alpha(alpha: Float): Modifier =
    this.then(Modifier.graphicsLayer { this.alpha = alpha })