package com.example.feature_auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.common.worker.ContactSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ContactViewModel @Inject constructor(
    private val workManager: WorkManager
) : ViewModel() {

    fun startSync() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val syncWork = OneTimeWorkRequestBuilder<ContactSyncWorker>()
            .setConstraints(constraints)
            .build()


        workManager.enqueueUniqueWork("contact_sync", ExistingWorkPolicy.REPLACE,syncWork)
    }
}