package com.example.feature_auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.navigati.navigation.LocalNavigator
import com.example.common.navigati.navigation.Screen
import com.example.ui.screen.Button.SocialIconButton
import com.example.ui.screen.Button.SubmitButton
import com.example.common.R as Common
import com.example.ui.R as Ui
import com.example.ui.theme.Black
import com.example.ui.theme.LightSageGray
import com.example.ui.theme.PaleMintGray
import com.example.ui.theme.SageGray
import com.example.ui.theme.White
import com.example.ui.theme.carosMedium
import com.example.ui.theme.carosThin
import com.example.ui.theme.onBoardingGradient
import kotlinx.coroutines.launch

@Composable
fun OnBoarding(){

    val clicked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.current


    Box(
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
            .background(
                brush = onBoardingGradient
            )
    )
    {

        Column(
            verticalArrangement = Arrangement.Top, horizontalAlignment = CenterHorizontally,
            modifier= Modifier.fillMaxWidth().padding(top = 17.dp, bottom =40.dp).padding(horizontal = 24.dp)
        )
        {
            Image(
                painter = painterResource(Ui.drawable.onboarding_ic),
                contentDescription = "OnBoarding",
                modifier = Modifier.height(100.dp).width(100.dp)
            )

            Text(
                text = stringResource(Common.string.connect_friends_easily_and_quickly),
                fontFamily = carosMedium,
                fontSize = 68.sp,
                lineHeight = 60.sp,
                color = White,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
            )
            Text(
                text =stringResource(Common.string.our_chat),
                fontFamily = carosThin,
                fontSize = 18.sp,
                color = SageGray,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 30.dp)
            )
            {

                SocialIconButton(
                    iconRes = Ui.drawable.fb_ic
                ) { }
                SocialIconButton(
                    iconRes = Ui.drawable.google_ic
                ) { }
                SocialIconButton(
                    iconRes = Ui.drawable.apple_ic
                ) { }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 30.dp)
            )
            {

                Divider(
                    modifier = Modifier.weight(1f),
                    color = LightSageGray,
                    thickness = 1.dp
                )

                Text(
                    text = "OR",
                    color = PaleMintGray,
                    fontSize = 14.sp,
                    fontFamily = carosThin,
                    modifier = Modifier.padding(horizontal = 7.dp)
                )

                Divider(
                    modifier = Modifier.weight(1f),
                    color = LightSageGray,
                    thickness = 1.dp
                )

            }

            Box(modifier = Modifier.padding(top = 30.dp)){

                SubmitButton(
                    text = stringResource(Common.string.sign_up),
                    isSelected = clicked,
                    onClick = {
                        scope.launch {
                            navigator.navigate(Screen.SignUp)
                        }
                    },
                    borderColor = White,
                    backgroundColor = White,
                    selectedBackgroundColor = White,
                    textColor = Black,
                    selectedTextColor = Black,
                )
            }

            Row(
                modifier = Modifier.padding(top = 20.dp).clickable{
                    scope.launch {
                        navigator.navigate(Screen.SignIn)
                    }
                },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Common.string.existing_account),
                    color = SageGray,
                    fontFamily = carosThin,
                    fontSize = 14.sp
                )

                Text(
                    text = stringResource(Common.string.sign_up),
                    color = White,
                    fontFamily = carosMedium,
                    fontSize = 14.sp
                )
            }

        }
    }
}