package com.example.ui.screens.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.service.BackupManager
import com.example.ui.components.JlabCard
import com.example.ui.components.JlabFilterChip
import com.example.ui.components.JlabOutlinedButton
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.components.JlabSectionCard
import com.example.ui.components.JlabTagBadge
import com.example.ui.components.JlabTextField
import com.example.ui.components.JlabTopBar
import com.example.ui.components.UserAvatar
import com.example.ui.theme.AppTheme
import com.example.util.FileStorageHelper
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    userName: String = "Geda",
    userBio: String = "Productivity Architect",
    userAvatarUri: String = "",
    homeBannerUri: String = "",
    homeBannerPosition: String = "COVER",
    onUpdateProfile: (String, String, String) -> Unit = { _, _, _ -> },
    onUpdateHomeBanner: (String, String) -> Unit = { _, _ -> },
    onClearHomeBanner: () -> Unit = {},
    themeMode: String,
    onSelectThemeMode: (String) -> Unit,
    isAppLockEnabled: Boolean,
    onToggleAppLock: (Boolean) -> Unit,
    onTriggerTestReminder: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isBackingUp by remember { mutableStateOf(false) }
    var isRestoring by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null) {
            isBackingUp = true
            coroutineScope.launch {
                val file = BackupManager.createBackup(context, uri)
                isBackingUp = false
                if (file != null) {
                    Toast.makeText(context, "Backup exported successfully!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to create backup.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            isRestoring = true
            coroutineScope.launch {
                val success = BackupManager.restoreBackup(context, uri)
                isRestoring = false
                if (success) {
                    Toast.makeText(
                        context,
                        "Restore successful! Restart JLab to reload database.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Invalid backup package or restore failed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    val homeBannerPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val copied = FileStorageHelper.copyUriToInternalStorage(context, uri, "home_banners")
            if (copied != null) {
                onUpdateHomeBanner(copied.absolutePath, homeBannerPosition)
                Toast.makeText(context, "Homepage banner updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val dbFile = context.getDatabasePath("jlab.db")
    val dbSizeKb = if (dbFile.exists()) dbFile.length() / 1024 else 0
    val vaultDir = File(context.filesDir, "vault")
    val vaultSizeKb = (vaultDir.listFiles()?.sumOf { it.length() } ?: 0L) / 1024

    Scaffold(
        containerColor = AppTheme.colorScheme.background,
        topBar = {
            JlabTopBar(
                title = "Control Center",
                subtitle = "Personal identity, appearance, security & backup",
                navigationIcon = if (onNavigateBack != null) Icons.AutoMirrored.Filled.ArrowBack else null,
                onNavigationClick = onNavigateBack,
                testTag = "settings_top_bar"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = AppTheme.spacing.screenHorizontal,
                    end = AppTheme.spacing.screenHorizontal,
                    top = AppTheme.spacing.screenVertical,
                    bottom = AppTheme.spacing.listBottomPadding
                )
                .testTag("settings_scroll_container"),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionGap)
        ) {
            // 1. Personal Profile Card
            JlabCard(testTag = "settings_profile_card") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        avatarUri = userAvatarUri,
                        name = userName,
                        size = AppTheme.spacing.avatarLg,
                        testTag = "settings_profile_avatar",
                        onClick = { showEditProfileDialog = true }
                    )

                    Spacer(modifier = Modifier.width(AppTheme.spacing.mdPlus))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            style = AppTheme.typography.titleMedium,
                            color = AppTheme.colorScheme.onSurface
                        )
                        Text(
                            text = userBio,
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    JlabOutlinedButton(
                        text = "Edit",
                        icon = Icons.Default.Edit,
                        onClick = { showEditProfileDialog = true },
                        testTag = "settings_edit_profile_button"
                    )
                }
            }

            // 2. Theme Appearance
            JlabSectionCard(
                title = "Theme Appearance",
                icon = Icons.Default.Palette,
                testTag = "settings_theme_card"
            ) {
                Text(
                    text = "Choose light, dark, or follow Android system default.",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(AppTheme.spacing.md))

                val options = listOf(
                    Triple("SYSTEM", "System Default", Icons.Default.BrightnessAuto),
                    Triple("LIGHT", "Light Mode", Icons.Default.LightMode),
                    Triple("DARK", "Dark Mode", Icons.Default.DarkMode)
                )

                options.forEach { (mode, label, icon) ->
                    val isSelected = themeMode.equals(mode, ignoreCase = true)
                    Surface(
                        shape = AppTheme.extendedShapes.button,
                        color = if (isSelected) {
                            AppTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        } else {
                            AppTheme.colorScheme.surfaceContainerLow
                        },
                        border = BorderStroke(
                            if (isSelected) AppTheme.spacing.borderSelected else AppTheme.spacing.borderThin,
                            if (isSelected) AppTheme.colorScheme.primary else AppTheme.extendedColors.cardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppTheme.spacing.xs)
                            .clip(AppTheme.extendedShapes.button)
                            .clickable { onSelectThemeMode(mode) }
                            .testTag("theme_mode_${mode.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = AppTheme.spacing.mdPlus,
                                    vertical = AppTheme.spacing.xs
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) {
                                        AppTheme.colorScheme.primary
                                    } else {
                                        AppTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(AppTheme.spacing.iconMd)
                                )
                                Spacer(modifier = Modifier.width(AppTheme.spacing.smPlus))
                                Text(
                                    text = label,
                                    style = AppTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) {
                                        AppTheme.colorScheme.primary
                                    } else {
                                        AppTheme.colorScheme.onSurface
                                    }
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectThemeMode(mode) }
                            )
                        }
                    }
                }
            }

            // 2B. Custom Homepage Graphic / Banner
            JlabSectionCard(
                title = "Custom Homepage Graphic",
                icon = Icons.Default.AddPhotoAlternate,
                testTag = "settings_banner_card"
            ) {
                Text(
                    text = "Personalize your Home workspace with a custom banner image or wallpaper graphic.",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(AppTheme.spacing.md))

                if (homeBannerUri.isNotBlank()) {
                    Card(
                        shape = AppTheme.extendedShapes.compactCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(AppTheme.extendedShapes.compactCard)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = homeBannerUri,
                                contentDescription = "Current Home Banner Preview",
                                contentScale = when (homeBannerPosition.uppercase()) {
                                    "CONTAIN" -> ContentScale.Fit
                                    else -> ContentScale.Crop
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                            JlabTagBadge(
                                text = "Active Graphic Preview",
                                containerColor = AppTheme.extendedColors.mediaScrim,
                                contentColor = AppTheme.extendedColors.onMediaOverlay,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(AppTheme.spacing.sm)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppTheme.spacing.smPlus))

                    Text(
                        text = "Display Style",
                        style = AppTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colorScheme.onSurface
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                        modifier = Modifier.padding(top = AppTheme.spacing.xs)
                    ) {
                        listOf(
                            "COVER" to "Crop Fill",
                            "CONTAIN" to "Fit Center",
                            "TOP" to "Top Align"
                        ).forEach { (pos, label) ->
                            JlabFilterChip(
                                label = label,
                                selected = homeBannerPosition.equals(pos, ignoreCase = true),
                                onClick = { onUpdateHomeBanner(homeBannerUri, pos) },
                                testTag = "banner_position_${pos.lowercase()}"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JlabOutlinedButton(
                            text = "Replace Image",
                            icon = Icons.Default.Edit,
                            onClick = {
                                homeBannerPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "banner_replace_button"
                        )

                        TextButton(
                            onClick = {
                                onClearHomeBanner()
                                Toast.makeText(
                                    context,
                                    "Restored default homepage appearance",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("banner_remove_button")
                        ) {
                            Text(
                                text = "Remove",
                                style = AppTheme.typography.labelLarge,
                                color = AppTheme.extendedColors.danger
                            )
                        }
                    }
                } else {
                    JlabOutlinedButton(
                        text = "Select Home Banner Graphic",
                        icon = Icons.Default.AddPhotoAlternate,
                        onClick = {
                            homeBannerPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "banner_select_button"
                    )
                }
            }

            // 3. Security & Privacy
            JlabSectionCard(
                title = "Security & Privacy",
                icon = Icons.Default.Security,
                testTag = "settings_security_card"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "App Lock (Biometric / PIN)",
                            style = AppTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Require system authentication to unlock JLab",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isAppLockEnabled,
                        onCheckedChange = onToggleAppLock,
                        modifier = Modifier.testTag("settings_app_lock_switch")
                    )
                }
            }

            // 4. Local Backup & Restore
            JlabSectionCard(
                title = "Backup & Restore",
                icon = Icons.Default.Backup,
                testTag = "settings_backup_card"
            ) {
                Text(
                    text = "Zero-cloud private portability. Export or import local database packages.",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(AppTheme.spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                ) {
                    JlabPrimaryButton(
                        text = if (isBackingUp) "Backing up…" else "Export Backup",
                        icon = Icons.Default.Upload,
                        enabled = !isBackingUp,
                        onClick = {
                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                            backupLauncher.launch("jlab_backup_$timestamp.zip")
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "settings_export_backup_button"
                    )

                    JlabOutlinedButton(
                        text = if (isRestoring) "Restoring…" else "Restore",
                        icon = Icons.Default.Download,
                        enabled = !isRestoring,
                        onClick = {
                            restoreLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "settings_restore_backup_button"
                    )
                }
            }

            // 5. Storage & Database
            JlabSectionCard(
                title = "Storage & Database",
                icon = Icons.Default.Storage,
                testTag = "settings_storage_card"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                    Text(
                        text = "Room SQLite Database: $dbSizeKb KB",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Internal File Vault: $vaultSizeKb KB",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Architecture: 100% Offline (Air-gapped)",
                        style = AppTheme.typography.labelLarge,
                        color = AppTheme.colorScheme.primary
                    )
                }
            }

            // 6. Offline Reminders Test
            JlabSectionCard(
                title = "Offline Reminders",
                icon = Icons.Default.NotificationsActive,
                testTag = "settings_reminders_card"
            ) {
                Text(
                    text = "System AlarmManager notification check for scheduled tasks.",
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(AppTheme.spacing.smPlus))
                JlabOutlinedButton(
                    text = "Trigger 10s Alarm Check",
                    icon = Icons.Default.NotificationsActive,
                    onClick = onTriggerTestReminder,
                    testTag = "settings_test_reminder_button"
                )
            }
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = userName,
            currentBio = userBio,
            currentAvatarUri = userAvatarUri,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, bio, avatarUri ->
                onUpdateProfile(name, bio, avatarUri)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentBio: String,
    currentAvatarUri: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(currentName) }
    var bio by remember { mutableStateOf(currentBio) }
    var avatarUri by remember { mutableStateOf(currentAvatarUri) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val saved = FileStorageHelper.copyUriToInternalStorage(context, uri, "avatars")
            avatarUri = saved?.absolutePath ?: uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppTheme.shapes.extraLarge,
        containerColor = AppTheme.colorScheme.surface,
        title = {
            Text(
                text = "Edit Profile Identity",
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppTheme.spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    UserAvatar(
                        avatarUri = avatarUri,
                        name = name,
                        size = AppTheme.spacing.avatarXl
                    )
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("profile_change_photo_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AppTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Change profile photo",
                                tint = AppTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(AppTheme.spacing.iconSm)
                            )
                        }
                    }
                }

                if (avatarUri.isNotBlank()) {
                    TextButton(
                        onClick = { avatarUri = "" },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(
                            text = "Remove Photo",
                            style = AppTheme.typography.labelMedium,
                            color = AppTheme.extendedColors.danger
                        )
                    }
                }

                JlabTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name",
                    singleLine = true,
                    testTag = "profile_name_input"
                )

                JlabTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "Title / Bio",
                    singleLine = true,
                    testTag = "profile_bio_input"
                )
            }
        },
        confirmButton = {
            JlabPrimaryButton(
                text = "Save Profile",
                enabled = name.isNotBlank(),
                onClick = { onSave(name.trim(), bio.trim(), avatarUri) },
                testTag = "profile_save_button"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(
                    text = "Cancel",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
