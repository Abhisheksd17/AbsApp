package com.example.feature_auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.R as ui
import com.example.common.R as common
import com.example.common.navigati.navigation.LocalNavigator
import com.example.common.navigation.Screen
import com.example.domain.data.NetworkResult
import com.example.feature_auth.viewmodel.LoginViewModel
import com.example.model.event.AuthEvent
import com.example.ui.screen.AppAlertDialog
import com.example.ui.screen.Button.SubmitButton
import com.example.ui.screen.InputField.OtpInput
import com.example.ui.theme.Black
import com.example.ui.theme.IceGray
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TealGreen
import com.example.ui.theme.White
import com.example.ui.theme.carosBold
import com.example.ui.theme.carosMedium
import kotlinx.coroutines.launch

@Composable
fun OtpVerification(){

    val viewModel: LoginViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.current
    val state by viewModel.verifyOtpState.collectAsStateWithLifecycle()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var otp by remember { mutableStateOf("") }

    val isFormValid=!otp.isEmpty()

    Box(
        modifier = Modifier
            .background(White).fillMaxWidth()
    ){

        Column(
            modifier = Modifier.padding(top=17.dp).fillMaxSize()
        )
        {

            Icon(
                painter = painterResource(id = ui.drawable.back_ic),
                contentDescription = null,
                modifier = Modifier.padding(24.dp).clickable{
                }
            )

            Text(
                text = stringResource(common.string.verify_phone),
                modifier = Modifier.padding(top = 60.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = carosBold,
                fontSize = 18.sp,
                color = Black
            )

            Text(
                text = stringResource(common.string.enter_digit),
                modifier = Modifier.padding(top = 16.dp).padding(horizontal = 24.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = carosMedium,
                fontSize = 14.sp,
                maxLines = 2,
                color = SlateGray
            )

            OtpInput(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                6,
                onOtpComplete={
                    otp=it
                }
            )

            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(common.string.did_not_recieve),
                    fontFamily = carosMedium,
                    fontSize = 14.sp,
                    color = SlateGray
                )

                Text(
                    text = stringResource(common.string.resend_otp),
                    fontFamily = carosMedium,
                    fontSize = 14.sp,
                    color = TealGreen,
                    modifier = Modifier.clickable {
                        // TODO: call resend API
                    }
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            SubmitButton(
                text = stringResource(common.string.verify),
                onClick = {
                    if(isFormValid){
                        scope.launch {
                            viewModel.verifyOtp(otp)
                        }
                    }
                },
                isSelected = isFormValid,
                borderColor = IceGray,
                backgroundColor = IceGray,
                selectedBackgroundColor = TealGreen,
                textColor = SlateGray,
                selectedTextColor = White,
                modifier = Modifier
                    .padding(horizontal = 24.dp).padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )



            if (showErrorDialog) {
                AppAlertDialog(
                    title = stringResource(common.string.error),
                    message = errorMessage,
                    onConfirm = { showErrorDialog = false },
                    onDismiss = { showErrorDialog = false }
                )
            }


        }

        when (state) {

            is NetworkResult.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TealGreen)
                }
            }

            is NetworkResult.Success -> {

            }

            is NetworkResult.Error -> {
                val message = (state as NetworkResult.Error).message
                LaunchedEffect(message) {
                    errorMessage = message ?: "Something went wrong"
                    showErrorDialog = true
                }
            }

            is NetworkResult.Idle->{

            }
        }

        LaunchedEffect(Unit) {
            viewModel.event.collect { event ->
                when (event) {
                    is AuthEvent.NavigateToOtp -> {

                    }
                    is AuthEvent.NavigateToHome -> {

                    }

                    is AuthEvent.NavigateToUpdateProfile -> {
                        navigator.navigate(Screen.Update)
                    }

                }
            }
        }
    }
}