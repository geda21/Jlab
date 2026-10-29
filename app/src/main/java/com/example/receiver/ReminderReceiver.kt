package com.example.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.service.NotificationHelper

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("EXTRA_TASK_ID") ?: return
        val taskTitle = intent.getStringExtra("EXTRA_TASK_TITLE") ?: "Task Reminder"
        val notificationId = taskId.hashCode()

        // Content intent: open app
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAV_TASK_ID", taskId)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Complete
        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "com.jlab.app.ACTION_COMPLETE_TASK"
            putExtra("EXTRA_TASK_ID", taskId)
            putExtra("EXTRA_TASK_TITLE", taskTitle)
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze 10m
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "com.jlab.app.ACTION_SNOOZE_TASK"
            putExtra("EXTRA_TASK_ID", taskId)
            putExtra("EXTRA_TASK_TITLE", taskTitle)
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Jlab Reminder")
            .setContentText(taskTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(taskTitle))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(0, "Complete", completePendingIntent)
            .addAction(0, "Snooze 10m", snoozePendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}
