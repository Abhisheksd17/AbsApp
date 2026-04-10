package com.example.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.navigation.Validator
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightSageGray
import com.example.ui.theme.TealGreen
import com.example.ui.theme.carosMedium
import com.example.ui.theme.carosThin

object InputField {


    @Composable
    fun CustomNameInputField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
    )
    {

        Column(modifier = modifier.fillMaxWidth()) {

            Text(
                text = label,
                color = TealGreen,
                fontSize = 14.sp,
                fontFamily = carosMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )

            Divider(
                color = LightSageGray,
                thickness = 1.dp
            )
        }
    }

    @Composable
    fun CustomPhoneInputField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
    ) {
        val isValid = Validator.isPhoneValid(value)
        val showError = value.isNotEmpty() && !isValid

        val color = if (showError) ErrorRed else TealGreen
        val dividerColor = if (showError) ErrorRed else LightSageGray

        Column(modifier = modifier.fillMaxWidth()) {


            Text(
                text = label,
                color = color,
                fontSize = 14.sp,
                fontFamily = carosMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            Divider(
                color = dividerColor,
                thickness = 1.dp
            )
            if (showError) {
                Text(
                    text = "Invalid phone number",
                    color = ErrorRed,
                    fontSize = 12.sp,
                    fontFamily = carosThin,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }

    @Composable
    fun CustomPasswordInputField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        isPassword: Boolean = false
    )
    {

        val isValid = Validator.isPasswordValid(value)
        val showError = value.isNotEmpty() && !isValid

        val color = if (showError) ErrorRed else TealGreen
        val dividerColor = if (showError) ErrorRed else LightSageGray

        Column(modifier = modifier.fillMaxWidth()) {

            Text(
                text = label,
                color = color,
                fontSize = 14.sp,
                fontFamily = carosMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                maxLines = 1,
                visualTransformation = if (isPassword)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isPassword)
                        KeyboardType.Password
                    else
                        KeyboardType.Text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )

            Divider(
                color = dividerColor,
                thickness = 1.dp
            )

            if (showError) {
                Text(
                    text = "Enter proper password",
                    color = ErrorRed,
                    fontSize = 12.sp,
                    fontFamily = carosThin,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}