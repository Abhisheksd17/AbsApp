package com.example.feature_contact.ui


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.ui.theme.SlateGray
import com.example.ui.R as ui
import com.example.ui.theme.White
import com.example.ui.theme.carosLight
import com.example.ui.theme.carosMedium
import com.example.ui.theme.circularStd

@Composable
fun ContactItem(
    id: Int,
    imageUri: String?,
    onImageSelected: (String) -> Unit,
    name: String,
    status: String?,
    onChatSelected:(Int)->Unit
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .clickable{onChatSelected(id)},
        verticalAlignment = Alignment.CenterVertically
    )
    {


        if (!imageUri.isNullOrEmpty()) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .clickable { onImageSelected(imageUri) }
            )
        } else {
            Image(
                painter = painterResource(id = ui.drawable.defaut_profile_ic),
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )
        }

        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                fontFamily = carosMedium,
                color = Black,
                fontSize = 18.sp
            )

            if (!status.isNullOrEmpty()) {
                Text(
                    text = status,
                    fontSize = 12.sp,
                    color = SlateGray,
                    fontFamily = circularStd
                )
            }
        }
    }
}