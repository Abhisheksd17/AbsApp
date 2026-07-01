package com.example.feature_profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.Black
import com.example.ui.theme.carosBold
import com.example.ui.theme.carosMedium
import com.example.ui.theme.circularStd
import com.example.ui.R as ui

@Composable
fun ProfileBoard(
    imageUri: String?,
    onImageSelected: (String) -> Unit,
    name: String,
    status: String,
    onProfileSelcted:()->Unit
){

        Row(
            modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 20.dp).clickable{onProfileSelcted()}
        ) {
            if (!imageUri.isNullOrEmpty()) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { onImageSelected(imageUri) }
                )
            } else {
                Image(
                    painter = painterResource(id = ui.drawable.defaut_profile_ic),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            }

            Column(
                modifier = Modifier.padding(start = 12.dp).align(Alignment.CenterVertically)
            ) {
                Text(
                    text = name,
                    fontFamily = carosBold,
                    color = Black,
                    fontSize = 20.sp
                )

                Text(
                    text = status,
                    fontFamily = circularStd,
                    color = Black,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = ui.drawable.qr_code_ic),
                contentDescription = null,
                modifier = Modifier.size(24.dp).align(Alignment.CenterVertically)
            )

        }

}