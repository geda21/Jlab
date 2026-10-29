package com.example.ui.screens.onboarding

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.JlabPrimaryButton
import com.example.ui.theme.AppTheme

data class OnboardingStep(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val steps = listOf(
        OnboardingStep(
            title = "Organize Everything in One Place",
            description = "Tasks, projects, plans, notes, knowledge, prompts, and focus time unified in one structured workspace.",
            icon = Icons.Default.Dashboard
        ),
        OnboardingStep(
            title = "100% Offline & Private",
            description = "No servers, no cloud sync, no tracking, and no external accounts. All your data lives exclusively on your device.",
            icon = Icons.Default.CloudOff
        ),
        OnboardingStep(
            title = "Backup Your Data Regularly",
            description = "Export full database archives (.zip) to local storage anytime for complete portability.",
            icon = Icons.Default.Save
        )
    )

    var currentStep by remember { mutableIntStateOf(0) }
    val step = steps[currentStep]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(AppTheme.spacing.xxl)
            .testTag("onboarding_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onFinish,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("onboarding_skip_button")
            ) {
                Text(
                    text = "Skip",
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colorScheme.primary
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .widthIn(max = 480.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = AppTheme.colorScheme.primary,
                    modifier = Modifier.size(AppTheme.spacing.minTouchTarget)
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.xxxl))

            Text(
                text = step.title,
                style = AppTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = AppTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.md))

            Text(
                text = step.description,
                style = AppTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = AppTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.xxxl))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.indices.forEach { index ->
                    val active = index == currentStep
                    Box(
                        modifier = Modifier
                            .size(
                                width = if (active) AppTheme.spacing.xl else AppTheme.spacing.sm,
                                height = AppTheme.spacing.sm
                            )
                            .clip(CircleShape)
                            .background(
                                if (active) AppTheme.colorScheme.primary
                                else AppTheme.colorScheme.surfaceContainerHighest
                            )
                    )
                }
            }
        }

        JlabPrimaryButton(
            text = if (currentStep == steps.size - 1) "Enter JLab" else "Next",
            icon = if (currentStep == steps.size - 1) {
                Icons.Default.Check
            } else {
                Icons.AutoMirrored.Filled.ArrowForward
            },
            onClick = {
                if (currentStep < steps.size - 1) {
                    currentStep++
                } else {
                    onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp),
            testTag = "onboarding_next_button"
        )
    }
}
