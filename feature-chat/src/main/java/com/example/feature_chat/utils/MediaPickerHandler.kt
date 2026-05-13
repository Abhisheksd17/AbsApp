package com.example.feature_chat.utils


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.FileProvider
import androidx.compose.ui.platform.LocalContext
import java.io.File

/**
 * Holds the launched URIs so the parent screen can react to picks.
 */
data class MediaPickerState(
    val pickedMediaUri: Uri? = null,
    val capturedImageUri: Uri? = null,
)

/**
 * Call this composable once at your screen level (e.g. inside Conversation).
 * It returns:
 *  - [state]         – the latest picked / captured URI
 *  - [launchGallery] – call to open the photo/video picker
 *  - [launchCamera]  – call to open the system camera
 */
@Composable
fun rememberMediaPicker(
    onMediaPicked: (PickedMedia) -> Unit = {},
    onPhotoCaptured: (Uri) -> Unit = {},
): Triple<MediaPickerState, () -> Unit, () -> Unit> {

    val context = LocalContext.current
    var state by remember { mutableStateOf(MediaPickerState()) }

    /* ── Gallery / Photo Picker ───────────────────────────────────────── */
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->

        uri?.let {

            state = state.copy(pickedMediaUri = it)

            val mimeType = context.contentResolver.getType(it)

            val mediaType = when {
                mimeType?.startsWith("image") == true -> "image"
                mimeType?.startsWith("video") == true -> "video"
                else -> "unknown"
            }

            onMediaPicked(
                PickedMedia(
                    uri = it,
                    type = mediaType
                )
            )
        }
    }

    /* ── Camera ───────────────────────────────────────────────────────── */
    // We need a stable URI to write the captured photo into before we launch.
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let {
                state = state.copy(capturedImageUri = it)
                onPhotoCaptured(it)
            }
        }
    }

    /* ── Helpers ──────────────────────────────────────────────────────── */
    val launchGallery: () -> Unit = {
        galleryLauncher.launch(
            androidx.activity.result.PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
            )
        )
    }

    val launchCamera: () -> Unit = {
        // Create a temp file and get a FileProvider URI for it
        val imageFile = File.createTempFile(
            "chat_photo_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",   // must match your manifest authority
            imageFile
        )
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    return Triple(state, launchGallery, launchCamera)
}