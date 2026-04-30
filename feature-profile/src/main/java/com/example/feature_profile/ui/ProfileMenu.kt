package com.example.feature_profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Black
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftLightGray
import com.example.ui.theme.carosBold
import com.example.ui.theme.carosMedium
import com.example.ui.theme.circularStd
import com.example.ui.R as ui

@Composable
fun ProfileMenu(
    header: String,
    info: String,
    icon:Int,
    onCLick: () -> Unit
){

    Row(modifier = Modifier.padding(bottom = 30.dp)){
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SoftLightGray)
                .clickable{onCLick()},
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp).clip(CircleShape)
            )
        }


        Column(
            modifier = Modifier.padding(start = 12.dp).align(Alignment.CenterVertically)
        ) {
            Text(
                text = header,
                fontFamily = carosMedium,
                color = Black,
                fontSize = 16.sp
            )

            Text(
                text = info,
                fontFamily = circularStd,
                color = Black,
                fontSize = 12.sp
            )
        }
    }

}