package com.capivv.sdk.ui.rescue

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capivv.sdk.ui.theme.CapivvTheme

/**
 * Steps in the rescue flow.
 */
enum class RescueFlowStep {
    SURVEY,
    FEEDBACK,
    OFFER,
    CONFIRM
}

/**
 * The rescue/cancellation flow screen.
 *
 * @param config Configuration for the flow
 * @param onResult Called when the flow completes with a result
 */
@Composable
fun RescueFlowScreen(
    config: RescueFlowConfig = RescueFlowConfig(),
    onResult: (RescueFlowResult) -> Unit
) {
    var currentStep by remember { mutableStateOf(RescueFlowStep.SURVEY) }
    var selectedReason by remember { mutableStateOf<CancellationReason?>(null) }
    var feedback by remember { mutableStateOf("") }

    CapivvTheme {
        Scaffold(
            topBar = {
                RescueFlowTopBar(
                    currentStep = currentStep,
                    onDismiss = { onResult(RescueFlowResult.Dismissed) }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "step"
                ) { step ->
                    when (step) {
                        RescueFlowStep.SURVEY -> SurveyStep(
                            config = config,
                            selectedReason = selectedReason,
                            onReasonSelected = { selectedReason = it },
                            onContinue = {
                                currentStep = if (config.showFeedbackStep) {
                                    RescueFlowStep.FEEDBACK
                                } else if (config.offers.isNotEmpty()) {
                                    RescueFlowStep.OFFER
                                } else {
                                    RescueFlowStep.CONFIRM
                                }
                            },
                            onSkip = if (config.allowSkipSurvey) {
                                { currentStep = RescueFlowStep.CONFIRM }
                            } else null
                        )

                        RescueFlowStep.FEEDBACK -> FeedbackStep(
                            config = config,
                            feedback = feedback,
                            onFeedbackChanged = { feedback = it },
                            onContinue = {
                                currentStep = if (config.offers.isNotEmpty()) {
                                    RescueFlowStep.OFFER
                                } else {
                                    RescueFlowStep.CONFIRM
                                }
                            },
                            onBack = { currentStep = RescueFlowStep.SURVEY }
                        )

                        RescueFlowStep.OFFER -> OfferStep(
                            config = config,
                            onAcceptOffer = { offer ->
                                onResult(RescueFlowResult.Saved(offer.id))
                            },
                            onDecline = { currentStep = RescueFlowStep.CONFIRM },
                            onBack = {
                                currentStep = if (config.showFeedbackStep) {
                                    RescueFlowStep.FEEDBACK
                                } else {
                                    RescueFlowStep.SURVEY
                                }
                            }
                        )

                        RescueFlowStep.CONFIRM -> ConfirmStep(
                            config = config,
                            onConfirmCancel = {
                                onResult(
                                    RescueFlowResult.Cancelled(
                                        reason = selectedReason?.id ?: "unknown",
                                        feedback = feedback.takeIf { it.isNotBlank() }
                                    )
                                )
                            },
                            onKeepSubscription = {
                                onResult(RescueFlowResult.Dismissed)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RescueFlowTopBar(
    currentStep: RescueFlowStep,
    onDismiss: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        },
        actions = {
            // Step indicator
            Row(
                modifier = Modifier.padding(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RescueFlowStep.entries.forEachIndexed { index, step ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (step.ordinal <= currentStep.ordinal) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                }
            }
        }
    )
}

@Composable
private fun SurveyStep(
    config: RescueFlowConfig,
    selectedReason: CancellationReason?,
    onReasonSelected: (CancellationReason) -> Unit,
    onContinue: () -> Unit,
    onSkip: (() -> Unit)?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = config.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        config.subtitle?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reason selection
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            config.reasons.forEach { reason ->
                ReasonCard(
                    reason = reason,
                    isSelected = selectedReason?.id == reason.id,
                    onClick = { onReasonSelected(reason) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinue,
            enabled = selectedReason != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }

        onSkip?.let {
            TextButton(
                onClick = it,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReasonCard(
    reason: CancellationReason,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reason.text,
                style = MaterialTheme.typography.bodyLarge
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FeedbackStep(
    config: RescueFlowConfig,
    feedback: String,
    onFeedbackChanged: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tell us more",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your feedback helps us improve",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = feedback,
            onValueChange = onFeedbackChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text(config.feedbackPlaceholder) },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun OfferStep(
    config: RescueFlowConfig,
    onAcceptOffer: (RescueOffer) -> Unit,
    onDecline: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Wait! We have a special offer",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We'd hate to see you go. Here's something special just for you.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Show offers
        config.offers.forEach { offer ->
            OfferCard(
                offer = offer,
                onAccept = { onAcceptOffer(offer) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onDecline,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("No thanks, continue cancellation")
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun OfferCard(
    offer: RescueOffer,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondary
            ) {
                Text(
                    text = when (offer.type) {
                        RescueOfferType.DISCOUNT -> "${offer.discountPercent}% OFF"
                        RescueOfferType.FREE_PERIOD -> "${offer.freeMonths} MONTHS FREE"
                        RescueOfferType.DOWNGRADE -> "SPECIAL PLAN"
                        RescueOfferType.PAUSE -> "PAUSE SUBSCRIPTION"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = offer.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = offer.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(offer.ctaText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ConfirmStep(
    config: RescueFlowConfig,
    onConfirmCancel: () -> Unit,
    onKeepSubscription: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = config.confirmTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = config.confirmMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Keep subscription button (primary action)
        Button(
            onClick = onKeepSubscription,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(config.confirmKeepText, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm cancel button (destructive)
        OutlinedButton(
            onClick = onConfirmCancel,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        ) {
            Text(config.confirmCancelText)
        }
    }
}
