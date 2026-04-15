package com.example.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.R as ui
import com.example.ui.theme.TealGreen
import com.example.ui.theme.White

@Composable
fun ImagePicker(
    modifier: Modifier,
    imageUri: Uri?,
    onImageSelected: (Uri) -> Unit
) {

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { onImageSelected(it) }
        }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier.size(100.dp)
        ) {

            AsyncImage(
                model = imageUri,
                contentDescription = null,
                placeholder = painterResource(ui.drawable.defaut_profile_ic),
                error = painterResource(ui.drawable.defaut_profile_ic),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.BottomEnd)
                    .offset(4.dp, 4.dp)
                    .background(TealGreen, CircleShape)
                    .clickable {
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = ui.drawable.camera_ic),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}