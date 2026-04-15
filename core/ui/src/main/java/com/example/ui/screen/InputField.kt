package com.example.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.util.Validator
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

            HorizontalDivider(
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
    )
    {
        val isValid = Validator.isPhoneValid(value)
        val showError = value.isNotEmpty() && !isValid

        val color = if (showError) ErrorRed else TealGreen
        val dividerColor = if (showError) ErrorRed else LightSageGray

        Column(modifier = modifier.fillMaxWidth()) {


            Text(
                text = label,
                color = color,
                fontSize = 14.sp,
                maxLines =1 ,
                fontFamily = carosMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        onValueChange(newValue)
                    }
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            HorizontalDivider(
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

            HorizontalDivider(
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


    @Composable
    fun OtpInput(
        modifier: Modifier = Modifier,
        otpLength: Int = 6,
        onOtpComplete: (String) -> Unit
    ) {
        var otp by remember { mutableStateOf("") }
        val onOtpCompleteRef by rememberUpdatedState(onOtpComplete)

        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        BasicTextField(
            value = otp,
            onValueChange = { newValue ->
                if (newValue.length <= otpLength && newValue.all(Char::isDigit)) {
                    otp = newValue
                    if (newValue.length == otpLength) {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onOtpCompleteRef(newValue)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            decorationBox = { _ ->
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        },
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(otpLength) { index ->
                        val char = otp.getOrNull(index)?.toString() ?: ""
                        val isFilled = index < otp.length
                        val isActive = index == otp.length

                        OtpCell(
                            char = char,
                            isFilled = isFilled,
                            isActive = isActive,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        )
    }

    @Composable
    private fun OtpCell(
        char: String,
        isFilled: Boolean,
        isActive: Boolean,
        modifier: Modifier = Modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
        val cursorAlpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "cursor_alpha"
        )

        val borderColor = when {
            isActive -> TealGreen
            isFilled -> TealGreen.copy(alpha = 0.5f)
            else -> Color.Gray.copy(alpha = 0.4f)
        }

        val borderWidth = if (isActive || isFilled) 2.dp else 1.dp

        Box(
            modifier = modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    if (isFilled) TealGreen.copy(alpha = 0.05f)
                    else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .alpha(cursorAlpha)
                        .background(TealGreen)
                )
            } else {
                Text(
                    text = char,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}