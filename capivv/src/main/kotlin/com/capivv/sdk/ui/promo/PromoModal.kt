package com.capivv.sdk.ui.promo

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * A promotional modal/dialog component.
 *
 * @param promotion The promotion to display
 * @param config Modal configuration
 * @param isVisible Whether the modal is visible
 * @param onClaim Called when the CTA is clicked
 * @param onDismiss Called when the modal is dismissed
 */
@Composable
fun PromoModal(
    promotion: Promotion,
    config: PromoModalConfig = PromoModalConfig(),
    isVisible: Boolean,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible && promotion.isActive) {
        Dialog(
            onDismissRequest = {
                if (config.dismissOnBackgroundTap) {
                    onDismiss()
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = config.dismissOnBackgroundTap
            )
        ) {
            PromoModalContent(
                promotion = promotion,
                config = config,
                onClaim = onClaim,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun PromoModalContent(
    promotion: Promotion,
    config: PromoModalConfig,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    val backgroundColor = promotion.backgroundColor?.let { parseColor(it) }
    val textColor = promotion.textColor?.let { parseColor(it) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (backgroundColor != null) {
                        Modifier.background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    backgroundColor,
                                    backgroundColor.copy(alpha = 0.8f)
                                )
                            )
                        )
                    } else {
                        Modifier.background(MaterialTheme.colorScheme.surface)
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                if (config.showCloseButton) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = textColor ?: MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Badge
                promotion.badgeText?.let { badge ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondary
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Icon
                Surface(
                    shape = CircleShape,
                    color = (textColor ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.1f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = textColor ?: MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                Text(
                    text = promotion.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = textColor ?: MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = promotion.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = (textColor ?: MaterialTheme.colorScheme.onSurface).copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Price display
                if (promotion.originalPrice != null && promotion.promoPrice != null) {
                    PriceComparison(
                        originalPrice = promotion.originalPrice,
                        promoPrice = promotion.promoPrice,
                        textColor = textColor
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Countdown timer
                if (config.showCountdown && promotion.hasCountdown) {
                    CountdownTimer(
                        endTime = promotion.getEndInstant()!!,
                        textColor = textColor
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // CTA Button
                Button(
                    onClick = onClaim,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (backgroundColor != null) {
                            (textColor ?: Color.White)
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        contentColor = if (backgroundColor != null) {
                            backgroundColor
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    )
                ) {
                    Text(
                        text = promotion.ctaText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Promo code
                promotion.promoCode?.let { code ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = (textColor ?: MaterialTheme.colorScheme.onSurface).copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Code:",
                                style = MaterialTheme.typography.bodySmall,
                                color = (textColor ?: MaterialTheme.colorScheme.onSurface).copy(alpha = 0.7f)
                            )
                            Text(
                                text = code,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = textColor ?: MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceComparison(
    originalPrice: String,
    promoPrice: String,
    textColor: Color?
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = originalPrice,
            style = MaterialTheme.typography.titleMedium,
            textDecoration = TextDecoration.LineThrough,
            color = (textColor ?: MaterialTheme.colorScheme.onSurface).copy(alpha = 0.5f)
        )
        Text(
            text = promoPrice,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun CountdownTimer(
    endTime: Instant,
    textColor: Color?
) {
    var timeRemaining by remember { mutableStateOf(calculateDetailedTimeRemaining(endTime)) }

    LaunchedEffect(endTime) {
        while (timeRemaining.totalSeconds > 0) {
            delay(1000)
            timeRemaining = calculateDetailedTimeRemaining(endTime)
        }
    }

    if (timeRemaining.totalSeconds <= 0) return

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = (textColor ?: MaterialTheme.colorScheme.onSurface).copy(alpha = 0.7f)
            )
            Text(
                text = "Offer ends in",
                style = MaterialTheme.typography.bodySmall,
                color = (textColor ?: MaterialTheme.colorScheme.onSurface).copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (timeRemaining.days > 0) {
                TimeUnit(value = timeRemaining.days, label = "days", textColor = textColor)
            }
            TimeUnit(value = timeRemaining.hours, label = "hrs", textColor = textColor)
            TimeUnit(value = timeRemaining.minutes, label = "min", textColor = textColor)
            TimeUnit(value = timeRemaining.seconds, label = "sec", textColor = textColor)
        }
    }
}

@Composable
private fun TimeUnit(
    value: Int,
    label: String,
    textColor: Color?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = (textColor ?: MaterialTheme.colorScheme.onSurface).copy(alpha = 0.1f)
        ) {
            Text(
                text = String.format("%02d", value),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor ?: MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = (textColor ?: MaterialTheme.colorScheme.onSurface).copy(alpha = 0.6f)
        )
    }
}

private data class DetailedTimeRemaining(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int
) {
    val totalSeconds: Long
        get() = days * 86400L + hours * 3600L + minutes * 60L + seconds
}

private fun calculateDetailedTimeRemaining(endTime: Instant): DetailedTimeRemaining {
    val now = Instant.now()
    if (now.isAfter(endTime)) {
        return DetailedTimeRemaining(0, 0, 0, 0)
    }

    var remaining = ChronoUnit.SECONDS.between(now, endTime)
    val days = (remaining / 86400).toInt()
    remaining %= 86400
    val hours = (remaining / 3600).toInt()
    remaining %= 3600
    val minutes = (remaining / 60).toInt()
    val seconds = (remaining % 60).toInt()

    return DetailedTimeRemaining(days, hours, minutes, seconds)
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
