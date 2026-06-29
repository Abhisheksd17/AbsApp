package com.example.feature_auth.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.navigati.navigation.LocalNavigator
import com.example.common.navigation.Screen
import com.example.domain.data.NetworkResult
import com.example.feature_auth.viewmodel.ContactViewModel
import com.example.feature_auth.viewmodel.FcmViewModel
import com.example.feature_auth.viewmodel.LoginViewModel
import com.example.model.event.AuthEvent
import com.example.model.login.UserResponse
import com.example.ui.screen.AppAlertDialog
import com.example.ui.screen.Button.SubmitButton
import com.example.ui.screen.ImagePicker
import com.example.ui.screen.InputField.CustomNameInputField
import com.example.ui.screen.InputField.CustomPhoneInputField
import com.example.ui.theme.IceGray
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TealGreen
import com.example.ui.R as ui
import com.example.common.R as common
import com.example.ui.theme.White
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@Composable
fun UpdateProfile(){


    val context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val contactViewModel: ContactViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.current
    val state by viewModel.profileState.collectAsState()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val tokenViewModel: FcmViewModel = hiltViewModel()




    val userProfile by viewModel.userProfile
        .collectAsStateWithLifecycle(
            initialValue = UserResponse(
                display_name = "",
                avatar_key = null,
                status_text = null,
                id = 0,
                is_online = false,
                last_seen_at = null
            )
        )


    LaunchedEffect(userProfile.id) {
        userName = userProfile.display_name
        status = userProfile.status_text ?: ""
    }


    val isFormValid=!(userName.isEmpty() || status.isEmpty() || imageUri.toString().isEmpty())


    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                contactViewModel.startSync()
            } else {
            }
        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }


    Box(
        modifier = Modifier
            .background(White).fillMaxWidth()
    ){
        Column(
            modifier = Modifier.padding(top=17.dp).fillMaxSize()
        ){
            Icon(
                painter = painterResource(id = ui.drawable.back_ic),
                contentDescription = null,
                modifier = Modifier.padding(24.dp).clickable{
                }
            )

            ImagePicker(
                modifier = Modifier
                    .padding(top = 60.dp),
                imageUri = imageUri,
                onImageSelected = { uri ->
                    imageUri = uri
                }
            )

            CustomNameInputField(
                value = userName,
                onValueChange = { userName = it },
                label = stringResource(common.string.name),
                modifier = Modifier.padding(top = 60.dp).padding(horizontal = 24.dp),
            )

            CustomNameInputField(
                value = status,
                onValueChange = { status = it },
                label = stringResource(common.string.info),
                modifier = Modifier.padding(top = 30.dp).padding(horizontal = 24.dp),
            )

            CustomPhoneInputField(
                value = "8105624198" ,
                onValueChange = {  },
                label = stringResource(common.string.mobile_no),
                modifier = Modifier.padding(top = 30.dp).padding(horizontal = 24.dp),
            )


            Spacer(
                modifier = Modifier.weight(1f)
            )

            SubmitButton(
                text = stringResource(common.string.update_profile),
                onClick = {
                    if(isFormValid){
                        scope.launch {
                            val file = uriToFile(context, imageUri)
                            viewModel.updateProfile(userName, status, file)
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


        when (state) {

            is NetworkResult.Idle->{

            }

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
                LaunchedEffect(Unit) {
                    viewModel.connectSocket()
                    try {
                        FirebaseMessaging.getInstance().token
                            .addOnSuccessListener { token ->

                                    tokenViewModel.registerFcmToken(token)

                            }
                    } catch (e: Exception) {
                        Log.e("UpdateProfile", "Firebase error", e)
                    }
                }
            }

            is NetworkResult.Error -> {
                val message = (state as NetworkResult.Error).message
                LaunchedEffect(message) {
                    errorMessage = message ?: "Something went wrong"
                    showErrorDialog = true
                }
            }
        }

        LaunchedEffect(Unit) {
            viewModel.event.collect { event ->
                when (event) {
                    is AuthEvent.NavigateToOtp -> {

                    }
                    is AuthEvent.NavigateToHome -> {
                        navigator.navigate(Screen.Chats)
                    }

                    is AuthEvent.NavigateToUpdateProfile -> {

                    }

                }
            }
        }

        if (showErrorDialog) {
            AppAlertDialog(
                title = stringResource(common.string.error),
                message = errorMessage,
                onConfirm = { showErrorDialog = false },
                onDismiss = { showErrorDialog = false }
            )
        }
    }


}

fun uriToFile(context: Context, uri: Uri?): File? {
    if (uri == null) return null

    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "upload.jpg")

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        file
    } catch (e: Exception) {
        null
    }
}