package com.example.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.JlabDatabase
import com.example.service.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("EXTRA_TASK_ID") ?: return
        val taskTitle = intent.getStringExtra("EXTRA_TASK_TITLE") ?: ""
        val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", taskId.hashCode())

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        val pendingResult = goAsync()
        val db = JlabDatabase.getInstance(context)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    "com.jlab.app.ACTION_COMPLETE_TASK" -> {
                        db.jlabDao().markTaskCompleted(taskId)
                    }
                    "com.jlab.app.ACTION_SNOOZE_TASK" -> {
                        val snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000L)
                        db.jlabDao().snoozeTask(taskId, snoozeTime)
                        ReminderScheduler.scheduleReminder(
                            context = context,
                            taskId = taskId,
                            title = taskTitle,
                            triggerTimeMillis = snoozeTime
                        )
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
