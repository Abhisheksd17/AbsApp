package com.example.feature_call.ui

import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AgoraVideoView(
    modifier: Modifier = Modifier,
    setup:    (SurfaceView) -> Unit,
) {
    AndroidView(
        factory  = { ctx ->
            SurfaceView(ctx).also { view ->
                view.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                setup(view)
            }
        },
        modifier = modifier
    )
}
