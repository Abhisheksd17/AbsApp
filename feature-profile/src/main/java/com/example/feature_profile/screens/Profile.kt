package com.example.feature_profile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ui.screen.HomeTopBar
import com.example.ui.theme.SoftLightGray
import com.example.ui.theme.White
import com.example.common.R as common

@Composable
fun Profile(){


    var isSearch by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ){
        Column(modifier = Modifier.fillMaxSize()) {

            HomeTopBar(
                header = stringResource(common.string.setting),
                search = isSearch,
                query = query,
                onQueryChange = { query = it },
                onSearchClick = { isSearch = !isSearch },
                onBackClick = {
                    isSearch = false
                    query = ""
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(White)
            ){

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 14.dp, bottom = 24.dp)
                        .width(30.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SoftLightGray.copy(alpha = 0.5f))
                )


            }

        }
    }
}