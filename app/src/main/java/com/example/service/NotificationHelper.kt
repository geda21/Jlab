package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationHelper {
    const val CHANNEL_REMINDERS = "jlab_reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Reminders Channel
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Jlab Reminders & Tasks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Offline task and project deadline notifications"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }
}
