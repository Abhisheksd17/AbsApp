package com.example.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.ui.theme.carosBold
import com.example.ui.theme.carosMedium

@Composable
fun AppAlertDialog(
    title: String,
    message: String,
    confirmText: String = "OK",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    showDismiss: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontFamily = carosBold)
        },
        text = {
            Text(text = message, fontFamily = carosMedium)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, fontFamily = carosMedium)
            }
        },
        dismissButton = {
            if (showDismiss) {
                TextButton(onClick = onDismiss) {
                    Text(dismissText, fontFamily = carosMedium)
                }
            }
        }
    )
}