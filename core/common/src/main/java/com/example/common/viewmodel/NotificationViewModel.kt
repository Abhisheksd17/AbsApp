package com.example.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.util.NotificationDestination
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _destination = MutableSharedFlow<NotificationDestination?>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val destination: SharedFlow<NotificationDestination?> = _destination.asSharedFlow()

    fun onNotification(dest: NotificationDestination) {
        viewModelScope.launch { _destination.emit(dest) }
    }

    fun clear() {
        viewModelScope.launch { _destination.emit(null) }
    }
}
