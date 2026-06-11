package com.example.feature_notifications

object NotificationActivityProvider {

    private var mainActivity:
            Class<*>? = null

    fun register(
        mainActivity: Class<*>
    ) {
        this.mainActivity = mainActivity
    }

    fun getMainActivity():
            Class<*> {

        return requireNotNull(mainActivity) {
            "MainActivity not registered"
        }
    }
}