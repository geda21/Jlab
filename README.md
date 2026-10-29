# Jlab — Offline Personal Digital Life Operating System

> **"My entire organized digital life inside one private application."**

Jlab is a 100% offline, local-first Android operating system designed to organize your entire digital life in one cohesive, distraction-free environment.

---

## Philosophy: CAPTURE → ORGANIZE → PLAN → EXECUTE → TRACK → REVIEW

Jlab unifies:
- **Task Management**: Prioritized tasks, quick todos, subtasks, repeat schedules, deadlines.
- **Projects**: Roadmaps, objectives, progress meters, linked tasks, decision records.
- **Plans & Locked Plans**: Structured daily, weekly, and monthly commitments. Includes **Locked Plans** with an explicit "UNLOCK" safety workflow to build iron discipline without accidental changes.
- **Structured Notes**: Category-driven notes with automatic autosave and zero data loss.
- **Information Vault**: Permanent reference, technical documentation, knowledge records.
- **Prompt Library**: Template engine supporting variables like `{{PRODUCT_NAME}}`, copy-to-clipboard, and share.
- **Business Workspace**: Ventures, value propositions, revenue models, product/service catalog.
- **Learning System**: Skills, courses, books, and study session loggers.
- **Social Content Pipeline**: Content planning across YouTube, LinkedIn, X, TikTok, and scripts.
- **Screenshot & File Vault**: Private on-device document and screenshot vault with system photo picker.
- **Local Calendar / Planner**: Completely offline agenda view for tasks, plans, and deadlines.
- **Focus Timer & Time Tracking**: Customizable sprint timers (15m, 25m, 45m, 60m) and duration logging.
- **Habits & Routines**: Consistency tracking with daily completion toggles.
- **Long-Term Goals**: Personal, financial, and technical targets with milestone progress.
- **Daily & Weekly Reviews**: Reflections on what went well and next priorities.
- **Global Search**: Instant offline query across all 11+ entity types.
- **Offline Backup & Restore**: Full SQLite database and attachment packaging via Android SAF to portable `.zip` archives.
- **Privacy & Security**: Biometric / device lock gate and optional `FLAG_SECURE` screenshot prevention.

---

## Architecture

- **Language**: Kotlin 100%
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Architecture Pattern**: MVVM + Repository Pattern
- **Local Database**: Room Database (SQLite) with reactive `Flow` observation
- **Preferences**: Jetpack DataStore Preferences
- **Alarms & Reminders**: Android `AlarmManager` with exact wakeup and `BroadcastReceiver` handlers
- **Notifications**: Notification channels with direct "Complete" and "Snooze 10m" action buttons
- **Network / Internet**: **NONE** (No internet permission requested or required)
- **External Servers / Analytics**: **NONE**

---

## Project Structure

```
com.example/
├── JlabApplication.kt             # Application initialization & locator
├── MainActivity.kt                # Root activity, Edge-to-Edge, navigation host
├── data/
│   ├── local/
│   │   ├── JlabDatabase.kt        # Room database definition
│   │   ├── JlabPreferences.kt     # DataStore preferences
│   │   ├── dao/JlabDao.kt         # Comprehensive Room DAO
│   │   └── entity/Entities.kt     # Domain entities
│   ├── model/Enums.kt             # Domain enums
│   └── repository/JlabRepository.kt # Central repository
├── receiver/
│   ├── ReminderReceiver.kt        # AlarmManager notification publisher
│   ├── NotificationActionReceiver.kt # Notification action buttons handler
│   └── BootReceiver.kt            # Reschedules alarms on device reboot
├── service/
│   ├── NotificationHelper.kt      # Notification channels
│   ├── ReminderScheduler.kt       # AlarmManager scheduler
│   └── BackupManager.kt           # Offline ZIP backup and restore
└── ui/
    ├── components/                # Reusable Compose components
    ├── screens/                   # Distinct modular screens
    ├── theme/                     # Jlab M3 Indigo / Cyan palette
    └── viewmodel/MainViewModel.kt # Central reactive ViewModel
```

---

## Permissions Used

- `android.permission.POST_NOTIFICATIONS`: Android 13+ local reminder notifications.
- `android.permission.RECEIVE_BOOT_COMPLETED`: Reschedule active alarms on device restart.
- `android.permission.SCHEDULE_EXACT_ALARM` & `android.permission.USE_EXACT_ALARM`: Punctual alarm delivery.
- `android.permission.VIBRATE`: Tactile feedback for alarms and focus timer completion.
- **Zero Internet Permission**: The app operates with zero network privileges.

---

## Build & APK Location

To build the debug APK:
```bash
gradle :app:assembleDebug
```

- Primary APK output: `app/build/outputs/apk/debug/app-debug.apk`
- Root copy: `Jlab-debug.apk`
