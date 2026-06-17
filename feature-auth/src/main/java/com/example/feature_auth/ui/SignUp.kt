package com.example.feature_auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.util.Validator
import com.example.common.navigati.navigation.LocalNavigator
import com.example.common.R as common
import com.example.common.navigation.Screen
import com.example.ui.screen.Button.SubmitButton
import com.example.ui.screen.InputField.CustomNameInputField
import com.example.ui.screen.InputField.CustomPasswordInputField
import com.example.ui.screen.InputField.CustomPhoneInputField
import com.example.ui.R as ui
import com.example.ui.theme.Black
import com.example.ui.theme.IceGray
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TealGreen
import com.example.ui.theme.White
import com.example.ui.theme.carosBold
import com.example.ui.theme.carosMedium
import kotlinx.coroutines.launch

@Composable
fun SignUp(){

    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.current


    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passWord by remember { mutableStateOf("") }
    var confirmPassWord by remember { mutableStateOf("") }

    val isFormValid =
        name.isNotBlank() &&
                Validator.isPhoneValid(phone) && Validator.isPasswordValid(passWord) &&
                passWord == confirmPassWord


    Box(
        modifier = Modifier
            .background(White).fillMaxSize()
    ){
        Column(
            modifier = Modifier.padding(top=17.dp).fillMaxSize().verticalScroll(rememberScrollState())
        )
        {
            Icon(
                painter = painterResource(id = ui.drawable.back_ic),
                contentDescription = null,
                modifier = Modifier.padding(24.dp).clickable{
                }
            )

            Text(
                text = stringResource(common.string.signup_with_mobile),
                modifier = Modifier.padding(top = 60.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = carosBold,
                fontSize = 18.sp,
                color = Black
            )

            Text(
                text = stringResource(common.string.get_chat_with_friends),
                modifier = Modifier.padding(top = 16.dp).padding(horizontal = 24.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = carosMedium,
                fontSize = 14.sp,
                maxLines = 2,
                color = SlateGray
            )


            CustomNameInputField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(common.string.name),
                modifier = Modifier.padding(top = 60.dp).padding(horizontal = 24.dp),
            )

            CustomPhoneInputField(
                value = phone,
                onValueChange = { phone = it },
                label = stringResource(common.string.mobile_no),
                modifier = Modifier.padding(top = 30.dp).padding(horizontal = 24.dp),
            )
            CustomPasswordInputField(
                value = passWord,
                onValueChange = { passWord = it },
                label = stringResource(common.string.password),
                modifier = Modifier.padding(top = 30.dp).padding(horizontal = 24.dp),
                isPassword = true
            )
            CustomPasswordInputField(
                value = confirmPassWord,
                onValueChange = { confirmPassWord = it },
                label = stringResource(common.string.confirm_password),
                modifier = Modifier.padding(top = 30.dp).padding(horizontal = 24.dp),
                isPassword = true
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            SubmitButton(
                text = stringResource(common.string.create_acc),
                onClick = {
                    if(isFormValid){
                        scope.launch {
                            navigator.navigate(Screen.Home)
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

        }

    }
}