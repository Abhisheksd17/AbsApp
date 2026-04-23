package com.example.absapp.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.absapp.R

import com.example.common.navigati.navigation.LocalNavigator
import com.example.absapp.ui.theme.White
import com.example.feature_auth.viewmodel.ContactViewModel
import com.example.common.datastore.DataStore
import com.example.common.navigation.Screen
import com.example.feature_auth.viewmodel.LoginViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject


@Composable
fun Splash(
) {


    val navigator = LocalNavigator.current
    var startAnimation by remember { mutableStateOf(false) }
    val viewModel: LoginViewModel = hiltViewModel()
    val contactViewModel: ContactViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()



    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(600)
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        val token = viewModel.getToken()
        if (token.isNullOrEmpty()) {
            navigator.navigate(Screen.OnBoarding)
        } else {
            navigator.navigate(Screen.Home)
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.splash_ic),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(154.dp)
                    .fillMaxWidth()
            )
        }
    }
}