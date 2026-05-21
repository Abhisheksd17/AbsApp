package com.example.feature_call.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.viewmodel.CallerViewModel
import com.example.domain.data.CallState
import com.example.feature_call.ui.AgoraVideoView
import com.example.feature_call.ui.CallStatusBar
import com.example.feature_call.ui.ModernCallControls
import com.example.feature_call.ui.WaitingCallState
import com.example.model.call.CallParams
import com.example.ui.theme.GradientEnd
import com.example.ui.theme.GradientStart
import com.example.ui.theme.LocalVideoBackground
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.delay

@Composable
fun Call(
    params: CallParams,
    onCallEnded: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: CallerViewModel = hiltViewModel()
    val callState by viewModel.callState.collectAsStateWithLifecycle()

    var engine by remember { mutableStateOf<RtcEngine?>(null) }
    var remoteUid by remember { mutableStateOf<Int?>(null) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var isCameraOn by remember { mutableStateOf(params.isVideo) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var callDuration by remember { mutableStateOf(0) }
    var isConnected by remember { mutableStateOf(false) }
    var controlsVisible by remember { mutableStateOf(true) }
    var permissionsGranted by remember { mutableStateOf(false) }
    var localVideoView by remember { mutableStateOf<android.view.SurfaceView?>(null) }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionsGranted = results.values.all { it }
    }

    LaunchedEffect(localVideoView, isCameraOn, engine) {
        val e = engine ?: return@LaunchedEffect
        val view = localVideoView ?: return@LaunchedEffect


        if (params.isVideo && isCameraOn) {
            e.enableLocalVideo(true)
            e.setupLocalVideo(
                VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0)
            )
            e.startPreview()
        } else {
            e.stopPreview()
            e.enableLocalVideo(false)
        }
    }

    LaunchedEffect(Unit) {
        permLauncher.launch(
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        )
    }

    LaunchedEffect(permissionsGranted) {
        if (!permissionsGranted) return@LaunchedEffect

        val config = RtcEngineConfig().apply {
            mContext = context
            mAppId = params.appId
            mEventHandler = object : IRtcEngineEventHandler() {

                override fun onUserJoined(uid: Int, elapsed: Int) {
                    remoteUid = uid
                    isConnected = true
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    remoteUid = null
                    isConnected = false
                    viewModel.notifyCallEnded(params.peerId, params.callId)
                    onCallEnded()
                }

                override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                    isConnected = true
                }

                override fun onError(err: Int) {
                    Log.e("Agora", "Error: $err")
                }
            }
        }

        engine = RtcEngine.create(config).apply {

            setEnableSpeakerphone(isSpeakerOn)

            if (params.isVideo) {
                enableLocalVideo(isCameraOn)
                enableVideo()
            }

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

    LaunchedEffect(isConnected) {
        if (!isConnected) return@LaunchedEffect
        while (true) {
            delay(1000L)
            callDuration++
        }
    }

    LaunchedEffect(callState) {
        if (callState is CallState.Ended) {
            onCallEnded()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            engine?.stopPreview()
            engine?.leaveChannel()
            RtcEngine.destroy()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GradientStart,
                        GradientEnd
                    )
                )
            )
            .clickable { controlsVisible = !controlsVisible }
    ) {

        remoteUid?.let { uid ->
            AgoraVideoView(
                modifier = Modifier.fillMaxSize(),
                setup = { view ->
                    engine?.setupRemoteVideo(
                        VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid)
                    )
                }
            )
        }

        if (remoteUid == null) {
            WaitingCallState(
                peerName = params.peerName,
                isCaller = params.isCaller,
                isConnected = isConnected
            )
        }

        if (params.isVideo && isCameraOn) {
            AgoraVideoView(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .size(width = 120.dp, height = 160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LocalVideoBackground),
                setup = { view ->
                    localVideoView = view
                }
            )
        }

        AnimatedVisibility(
            visible = controlsVisible,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        ) {
            CallStatusBar(
                peerName = params.peerName,
                duration = if (isConnected) callDuration.toFormattedDuration() else "",
                isConnected = isConnected
            )
        }

        AnimatedVisibility(
            visible = controlsVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ModernCallControls(
                isMuted = isMuted,
                isSpeakerOn = isSpeakerOn,
                isCameraOn = isCameraOn,
                isVideo = params.isVideo,
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
                },
                onSwitchCamera = {
                    isFrontCamera = !isFrontCamera
                    engine?.switchCamera()
                },
                onHangUp = {
                    engine?.stopPreview()
                    engine?.leaveChannel()
                    viewModel.notifyCallEnded(params.peerId, params.callId)
                }
            )
        }
    }
}

private fun Int.toFormattedDuration(): String {
    val h = this / 3600
    val m = (this % 3600) / 60
    val s = this % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s)
    else "%02d:%02d".format(m, s)
}