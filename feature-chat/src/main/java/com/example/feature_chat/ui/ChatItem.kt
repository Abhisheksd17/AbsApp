package com.example.feature_chat.ui

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.R as ui
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Black
import com.example.ui.theme.CoolSlateGreen
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TealGreen
import com.example.ui.theme.White
import com.example.ui.theme.carosBold
import com.example.ui.theme.carosLight
import com.example.ui.theme.carosMedium
import com.example.ui.theme.carosThin
import com.example.ui.theme.circularStd

@Composable
fun ChatItem(
    imageUri: String?,
    onImageSelected: (Uri) -> Unit,
    name:String,
    message:String?,
    timesAgo:String?,
    unreadMsg:Boolean,
    msgCount:Int?
){

    Box(
        modifier = Modifier.fillMaxWidth().background(White).padding(bottom = 10.dp).padding(horizontal = 24.dp)
    ){

        Row(

        ) {
            Box(
                modifier = Modifier.size(52.dp)
            ) {

                Image(
                    painter = painterResource(id = ui.drawable.defaut_profile_ic),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize()
                )

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.BottomEnd)
                        .background(TealGreen, CircleShape)
                )
            }

            Column(
                modifier = Modifier.padding(start = 12.dp)
            )
            {

                Text(
                    text = name?:"",
                    fontFamily = carosMedium,
                    color = Black,
                    fontSize = 15.sp
                    )

                Text(
                    modifier = Modifier.padding(top = 3.dp),
                    text = message?:"",
                    fontSize = 10.sp,
                    color = SlateGray,
                    fontFamily = circularStd

                )

            }


            Spacer(
                modifier = Modifier.weight(1f)
            )

            if(unreadMsg){
                Column(
                    horizontalAlignment = Alignment.End

                )
                {
                    Text(
                        text = timesAgo?:"",
                        fontSize = 10.sp,
                        fontFamily = carosLight

                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(ErrorRed, CircleShape)
                            .clickable {  },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = msgCount.toString() ?:"",
                            fontSize = 10.sp,
                            fontFamily = carosBold,
                            color = White
                        )
                    }
                }
            }

        }
        }

}