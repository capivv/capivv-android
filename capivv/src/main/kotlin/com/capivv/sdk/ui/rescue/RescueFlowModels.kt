package com.capivv.sdk.ui.rescue

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Reason for cancellation.
 */
@Serializable
data class CancellationReason(
    val id: String,
    val text: String,
    val icon: String? = null
)

/**
 * A rescue offer to present to the user.
 */
@Serializable
data class RescueOffer(
    val id: String,
    val type: RescueOfferType,
    val title: String,
    val description: String,
    @SerialName("discount_percent")
    val discountPercent: Int? = null,
    @SerialName("free_months")
    val freeMonths: Int? = null,
    @SerialName("product_id")
    val productId: String? = null,
    @SerialName("cta_text")
    val ctaText: String = "Accept Offer"
)

/**
 * Type of rescue offer.
 */
@Serializable
enum class RescueOfferType {
    @SerialName("discount")
    DISCOUNT,

    @SerialName("free_period")
    FREE_PERIOD,

    @SerialName("downgrade")
    DOWNGRADE,

    @SerialName("pause")
    PAUSE
}

/**
 * Configuration for the rescue flow.
 */
data class RescueFlowConfig(
    val title: String = "We're sorry to see you go",
    val subtitle: String? = "Before you cancel, tell us why so we can improve",
    val reasons: List<CancellationReason> = defaultReasons,
    val offers: List<RescueOffer> = emptyList(),
    val allowSkipSurvey: Boolean = false,
    val showFeedbackStep: Boolean = true,
    val feedbackPlaceholder: String = "Tell us more about your experience...",
    val confirmTitle: String = "Are you sure?",
    val confirmMessage: String = "You'll lose access to all premium features when your subscription ends.",
    val confirmCancelText: String = "Yes, Cancel",
    val confirmKeepText: String = "Keep Subscription"
) {
    companion object {
        val defaultReasons = listOf(
            CancellationReason("too_expensive", "Too expensive"),
            CancellationReason("not_using", "Not using it enough"),
            CancellationReason("missing_features", "Missing features I need"),
            CancellationReason("found_alternative", "Found a better alternative"),
            CancellationReason("technical_issues", "Technical issues"),
            CancellationReason("other", "Other reason")
        )
    }
}

/**
 * Result of the rescue flow.
 */
sealed class RescueFlowResult {
    data class Saved(val offerId: String) : RescueFlowResult()
    data class Cancelled(val reason: String, val feedback: String?) : RescueFlowResult()
    object Dismissed : RescueFlowResult()
}

/**
 * Events tracked during the rescue flow.
 */
object RescueFlowEvents {
    const val FLOW_STARTED = "rescue_flow_started"
    const val REASON_SELECTED = "rescue_reason_selected"
    const val FEEDBACK_SUBMITTED = "rescue_feedback_submitted"
    const val OFFER_VIEWED = "rescue_offer_viewed"
    const val OFFER_ACCEPTED = "rescue_offer_accepted"
    const val OFFER_DECLINED = "rescue_offer_declined"
    const val CANCELLATION_CONFIRMED = "rescue_cancellation_confirmed"
    const val FLOW_DISMISSED = "rescue_flow_dismissed"
    const val SUBSCRIPTION_KEPT = "rescue_subscription_kept"
}
