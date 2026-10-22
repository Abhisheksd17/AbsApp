package com.example.feature_chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.R
import com.example.ui.theme.Black
import com.example.ui.theme.SlateGray
import com.example.ui.R as ui
import com.example.ui.theme.White
import com.example.ui.theme.carosMedium
import com.example.ui.theme.circularStd

@Composable
fun AiChatTopBar(
    onBackClick : () -> Unit
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = ui.drawable.back_ic),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() },
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(12.dp))
        Image(
            painter = painterResource(id = ui.drawable.defaut_profile_ic),
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
        )

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp).align(Alignment.CenterVertically)) {


            Text(
                text = stringResource(R.string.absbot),
                fontSize = 16.sp,
                color = Black,
                fontFamily = carosMedium
            )

            Text(
                text =stringResource(R.string.available) ,
                fontSize = 12.sp,
                color = SlateGray,
                fontFamily =circularStd
            )
        }
    }

}