package com.example.absapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.cloudinary.android.MediaManager
import com.example.feature_notifications.CallNotificationObserver
import com.example.feature_notifications.NotificationActivityProvider
import com.example.feature_notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AbsApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var callNotificationObserver: CallNotificationObserver

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        val config = mapOf(
            "cloud_name" to "dujzbrfam"
        )
        callNotificationObserver.start()
        NotificationActivityProvider.register(
            mainActivity = MainActivity::class.java
        )

        NotificationHelper.createChannels(this)
        MediaManager.init(this, config)
        net.sqlcipher.database.SQLiteDatabase.loadLibs(this)
    }
}