package com.capivv.sdk.ui.promo

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * A promotional banner component.
 *
 * @param promotion The promotion to display
 * @param config Banner configuration
 * @param onClick Called when the banner is clicked
 * @param onDismiss Called when the banner is dismissed
 * @param modifier Modifier for the banner
 */
@Composable
fun PromoBanner(
    promotion: Promotion,
    config: PromoBannerConfig = PromoBannerConfig(),
    onClick: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }

    // Auto-hide timer
    config.autoHideSeconds?.let { seconds ->
        LaunchedEffect(promotion.id) {
            delay(seconds * 1000L)
            isVisible = false
            onDismiss?.invoke()
        }
    }

    AnimatedVisibility(
        visible = isVisible && promotion.isActive,
        enter = slideInVertically(
            initialOffsetY = { if (config.position == BannerPosition.TOP) -it else it }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { if (config.position == BannerPosition.TOP) -it else it }
        ) + fadeOut()
    ) {
        val backgroundColor = promotion.backgroundColor?.let { parseColor(it) }
            ?: MaterialTheme.colorScheme.primary
        val textColor = promotion.textColor?.let { parseColor(it) }
            ?: MaterialTheme.colorScheme.onPrimary

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() },
            color = backgroundColor,
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Column {
                        Text(
                            text = promotion.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        if (config.showCountdown && promotion.hasCountdown) {
                            Spacer(modifier = Modifier.height(2.dp))
                            CountdownText(
                                endTime = promotion.getEndInstant()!!,
                                textColor = textColor.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Discount badge
                    promotion.discountPercent?.let { percent ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = textColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "-$percent%",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }

                    // Dismiss button
                    if (config.dismissible && onDismiss != null) {
                        IconButton(
                            onClick = {
                                isVisible = false
                                onDismiss()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = textColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownText(
    endTime: Instant,
    textColor: Color
) {
    var timeRemaining by remember { mutableStateOf(calculateTimeRemaining(endTime)) }

    LaunchedEffect(endTime) {
        while (timeRemaining.totalSeconds > 0) {
            delay(1000)
            timeRemaining = calculateTimeRemaining(endTime)
        }
    }

    if (timeRemaining.totalSeconds > 0) {
        Text(
            text = "Ends in ${formatTimeRemaining(timeRemaining)}",
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

private data class TimeRemaining(
    val hours: Int,
    val minutes: Int,
    val seconds: Int
) {
    val totalSeconds: Long
        get() = hours * 3600L + minutes * 60L + seconds
}

private fun calculateTimeRemaining(endTime: Instant): TimeRemaining {
    val now = Instant.now()
    if (now.isAfter(endTime)) {
        return TimeRemaining(0, 0, 0)
    }

    var remaining = ChronoUnit.SECONDS.between(now, endTime)
    val hours = (remaining / 3600).toInt()
    remaining %= 3600
    val minutes = (remaining / 60).toInt()
    val seconds = (remaining % 60).toInt()

    return TimeRemaining(hours, minutes, seconds)
}

private fun formatTimeRemaining(time: TimeRemaining): String {
    return if (time.hours > 0) {
        String.format("%02d:%02d:%02d", time.hours, time.minutes, time.seconds)
    } else {
        String.format("%02d:%02d", time.minutes, time.seconds)
    }
}

private fun parseColor(colorString: String): Color? {
    return try {
        Color(android.graphics.Color.parseColor(
            if (colorString.startsWith("#")) colorString else "#$colorString"
        ))
    } catch (e: Exception) {
        null
    }
}
