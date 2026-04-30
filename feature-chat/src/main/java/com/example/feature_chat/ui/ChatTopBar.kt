package com.example.feature_chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.Black
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.SlateGray
import com.example.ui.R as ui
import com.example.ui.theme.White
import com.example.ui.theme.carosMedium
import com.example.ui.theme.circularStd

@Composable
fun ChatTopBar(
    imageUri: String = "",
    name: String ,
    status: String ,
    isOnline: Boolean,
    onBackClick: () -> Unit = {},
    onCallClick: () -> Unit = {},
    onVideoClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {

        Icon(
            painter = painterResource(id = ui.drawable.back_ic),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() },
                    tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box {
            if (imageUri.isNotEmpty()) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
            }else
            {
                Image(
                    painter = painterResource(id = ui.drawable.defaut_profile_ic),
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
            }

            if(isOnline){

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(OnlineGreen)
                        .border(2.dp, White, CircleShape)
                )
            }

        }


        Column(modifier = Modifier.weight(1f).padding(start = 12.dp).align(Alignment.CenterVertically)) {


            Text(
                text = name,
                fontSize = 16.sp,
                color = Black,
                fontFamily = carosMedium
            )

            Text(
                text = status,
                fontSize = 12.sp,
                color = SlateGray,
                fontFamily =circularStd
            )
        }

        Icon(
            painter = painterResource(id = ui.drawable.dark_call_ic),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { onCallClick() },
               tint = Color.Unspecified
        )


        Icon(
            painter = painterResource(id = ui.drawable.video_ic),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(30.dp)
                .clickable { onVideoClick() },
            tint = Color.Unspecified
        )
    }
}