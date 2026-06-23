package com.example.feature_notifications

import android.content.Context
import android.util.Log
import kotlinx.coroutines.launch
import com.example.domain.repository.CallRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallNotificationObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: CallRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

     fun start() {
        scope.launch {
            repository.observeCallAccepted().collect {
                Log.d("CallNotificationObserver", "Call accepted")
                CallNotificationManager.dismissIncomingCallNotification(context)
            }
        }
        scope.launch {
            repository.observeCallEnded().collect {
                Log.d("CallNotificationObserver", "Call ended")

                CallNotificationManager.dismissIncomingCallNotification(context)
            }
        }
    }
}