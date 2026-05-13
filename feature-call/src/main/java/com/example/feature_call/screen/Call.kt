package com.example.feature_call.screens

import android.Manifest
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CallEnd
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.VideocamOff
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.viewmodel.CallerViewModel
import com.example.model.call.CallParams
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

@Composable
fun Call(
    params: CallParams,
    onCallEnded: () -> Unit,
) {
    val context = LocalContext.current

    var engine          by remember { mutableStateOf<RtcEngine?>(null) }
    var remoteUid       by remember { mutableStateOf<Int?>(null) }
    var isMuted         by remember { mutableStateOf(false) }
    var isSpeakerOn     by remember { mutableStateOf(true) }
    var isCameraOn      by remember { mutableStateOf(params.isVideo) }
    var isFrontCamera   by remember { mutableStateOf(true) }
    var callDuration    by remember { mutableStateOf(0) }
    var isConnected     by remember { mutableStateOf(false) }
    var controlsVisible by remember { mutableStateOf(true) }
    var permissionsGranted by remember { mutableStateOf(false) }
    val viewModel: CallerViewModel= hiltViewModel()

    // ── Permissions ───────────────────────────────────────────────────────────
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionsGranted = results.values.all { it }
    }

    LaunchedEffect(Unit) {
        permLauncher.launch(
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        )
    }

    // ── Init Agora — only after permissions granted ───────────────────────────
    LaunchedEffect(permissionsGranted) {
        if (!permissionsGranted) return@LaunchedEffect

        val config = RtcEngineConfig().apply {
            mContext      = context
            mAppId        = params.appId
            mEventHandler = object : IRtcEngineEventHandler() {

                override fun onUserJoined(uid: Int, elapsed: Int) {
                    remoteUid   = uid
                    isConnected = true
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    remoteUid   = null
                    isConnected = false
                    // Peer left — notify server then pop screen
                    viewModel.notifyCallEnded(params.peerId, params.callId)
                    onCallEnded()
                }

                override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                    isConnected = true
                }

                override fun onError(err: Int) { /* log / show snackbar */ }
            }
        }

        engine = RtcEngine.create(config).apply {
            enableVideo()
            setEnableSpeakerphone(isSpeakerOn)
            if (params.isVideo) startPreview()
            joinChannel(
                params.token,
                params.channel,
                params.uid,
                ChannelMediaOptions().apply {
                    clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
                    channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
                }
            )
        }
    }

    // ── Timer — starts only once remote peer joins ────────────────────────────
    LaunchedEffect(isConnected) {
        if (!isConnected) return@LaunchedEffect
        while (true) {
            kotlinx.coroutines.delay(1000L)
            callDuration++
        }
    }

    // ── Cleanup on exit ───────────────────────────────────────────────────────
    DisposableEffect(Unit) {
        onDispose {
            engine?.leaveChannel()
            RtcEngine.destroy()
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .clickable { controlsVisible = !controlsVisible }
    ) {

        // Remote video — full screen
        remoteUid?.let { uid ->
            AgoraVideoView(
                modifier = Modifier.fillMaxSize(),
                setup    = { view ->
                    engine?.setupRemoteVideo(
                        VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid)
                    )
                }
            )
        }

        // Waiting placeholder — shown until remote joins
        if (remoteUid == null) {
            Column(
                modifier            = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text       = params.peerName,
                    color      = Color.White,
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text     = if (params.isCaller) "Ringing…" else "Connecting…",
                    color    = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                )
            }
        }

        // Local PiP — top-right corner
        if (params.isVideo && isCameraOn) {
            AgoraVideoView(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(width = 100.dp, height = 140.dp)
                    .clip(RoundedCornerShape(16.dp)),
                setup = { view ->
                    engine?.setupLocalVideo(
                        VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0)
                    )
                }
            )
        }

        // Controls — fade in/out on tap
        AnimatedVisibility(
            visible  = controlsVisible,
            enter    = fadeIn(),
            exit     = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            CallControls(
                isMuted      = isMuted,
                isSpeakerOn  = isSpeakerOn,
                isCameraOn   = isCameraOn,
                isVideo      = params.isVideo,
                callDuration = if (isConnected) callDuration.toFormattedDuration() else "",
                peerName     = params.peerName,
                onToggleMute = {
                    isMuted = !isMuted
                    engine?.muteLocalAudioStream(isMuted)
                },
                onToggleSpeaker = {
                    isSpeakerOn = !isSpeakerOn
                    engine?.setEnableSpeakerphone(isSpeakerOn)
                },
                onToggleCamera = {
                    isCameraOn = !isCameraOn
                    engine?.muteLocalVideoStream(!isCameraOn)
                },
                onSwitchCamera = {
                    isFrontCamera = !isFrontCamera
                    engine?.switchCamera()
                },
                onHangUp = {
                    engine?.leaveChannel()
                    viewModel.notifyCallEnded(params.peerId, params.callId)
                    onCallEnded()
                },
            )
        }
    }
}

// ── Agora SurfaceView wrapper ─────────────────────────────────────────────────

@Composable
fun AgoraVideoView(
    modifier: Modifier = Modifier,
    setup:    (SurfaceView) -> Unit,
) {
    AndroidView(
        modifier = modifier,
        factory  = { ctx ->
            SurfaceView(ctx).also { view ->
                view.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                )
                setup(view)
            }
        }
    )
}

// ── Controls bar ──────────────────────────────────────────────────────────────

@Composable
private fun CallControls(
    isMuted:         Boolean,
    isSpeakerOn:     Boolean,
    isCameraOn:      Boolean,
    isVideo:         Boolean,
    callDuration:    String,
    peerName:        String,
    onToggleMute:    () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleCamera:  () -> Unit,
    onSwitchCamera:  () -> Unit,
    onHangUp:        () -> Unit,
) {
    Column(
        modifier            = Modifier
            .background(
                Color.Black.copy(alpha = 0.6f),
                RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            )
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(peerName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        if (callDuration.isNotEmpty()) {
            Text(callDuration, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }

        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier              = Modifier.fillMaxWidth(),
        ) {
            CallButton(
                icon    = if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                label   = if (isMuted) "Unmute" else "Mute",
                tint    = if (isMuted) Color(0xFFFF5252) else Color.White,
                onClick = onToggleMute,
            )
            if (isVideo) {
                CallButton(
                    icon    = if (isCameraOn) Icons.Rounded.Videocam else Icons.Rounded.VideocamOff,
                    label   = if (isCameraOn) "Camera" else "No cam",
                    tint    = if (!isCameraOn) Color(0xFFFF5252) else Color.White,
                    onClick = onToggleCamera,
                )
                CallButton(
                    icon    = Icons.Rounded.FlipCameraAndroid,
                    label   = "Flip",
                    tint    = Color.White,
                    onClick = onSwitchCamera,
                )
            }
            CallButton(
                icon    = if (isSpeakerOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                label   = "Speaker",
                tint    = if (!isSpeakerOn) Color(0xFFFF5252) else Color.White,
                onClick = onToggleSpeaker,
            )
            // End call
            Box(
                modifier         = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30))
                    .clickable(onClick = onHangUp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Rounded.CallEnd,
                    contentDescription = "End call",
                    tint               = Color.White,
                    modifier           = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun CallButton(
    icon:    ImageVector,
    label:   String,
    tint:    Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier            = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier         = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
    }
}

// ── Util ──────────────────────────────────────────────────────────────────────

private fun Int.toFormattedDuration(): String {
    val h = this / 3600
    val m = (this % 3600) / 60
    val s = this % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s)
    else              "%02d:%02d".format(m, s)
}