package com.example

import android.app.Application
import com.example.data.local.JlabDatabase
import com.example.data.local.JlabPreferences
import com.example.data.repository.JlabRepository
import com.example.service.NotificationHelper

class JlabApplication : Application() {

    lateinit var database: JlabDatabase
        private set

    lateinit var repository: JlabRepository
        private set

    lateinit var preferences: JlabPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        NotificationHelper.createNotificationChannels(this)
        database = JlabDatabase.getInstance(this)
        repository = JlabRepository(database.jlabDao())
        preferences = JlabPreferences(this)
    }

    companion object {
        lateinit var instance: JlabApplication
            private set
    }
}
