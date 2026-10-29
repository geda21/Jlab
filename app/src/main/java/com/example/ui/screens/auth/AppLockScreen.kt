package com.example.ui.screens.auth

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.theme.AppTheme

@Composable
fun AppLockScreen(
    onUnlockSuccess: () -> Unit
) {
    val context = LocalContext.current
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onUnlockSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.background)
            .systemBarsPadding()
            .testTag("app_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(AppTheme.spacing.xxxl)
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.spacing.avatarXl)
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Workspace locked",
                    tint = AppTheme.colorScheme.primary,
                    modifier = Modifier.size(AppTheme.spacing.iconXl)
                )
            }

            Text(
                text = "JLab is Locked",
                style = AppTheme.typography.headlineMedium,
                color = AppTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Your offline personal workspace is protected by device authentication.",
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

            JlabPrimaryButton(
                text = "Unlock JLab",
                icon = Icons.Default.Fingerprint,
                onClick = {
                    if (keyguardManager != null && keyguardManager.isDeviceSecure) {
                        @Suppress("DEPRECATION")
                        val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                            "Unlock JLab",
                            "Authenticate to access your private data"
                        )
                        if (intent != null) {
                            authLauncher.launch(intent)
                        } else {
                            onUnlockSuccess()
                        }
                    } else {
                        onUnlockSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "unlock_app_button"
            )
        }
    }
}
