package com.capivv.sdk.ui.promo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * A promotional offer.
 */
@Serializable
data class Promotion(
    val id: String,
    val title: String,
    val description: String,
    @SerialName("discount_percent")
    val discountPercent: Int? = null,
    @SerialName("discount_amount")
    val discountAmount: Double? = null,
    @SerialName("promo_code")
    val promoCode: String? = null,
    @SerialName("product_id")
    val productId: String? = null,
    @SerialName("original_price")
    val originalPrice: String? = null,
    @SerialName("promo_price")
    val promoPrice: String? = null,
    @SerialName("start_time")
    val startTime: String? = null,
    @SerialName("end_time")
    val endTime: String? = null,
    @SerialName("cta_text")
    val ctaText: String = "Claim Offer",
    @SerialName("background_color")
    val backgroundColor: String? = null,
    @SerialName("text_color")
    val textColor: String? = null,
    @SerialName("badge_text")
    val badgeText: String? = null,
    val metadata: Map<String, String>? = null
) {
    /**
     * Whether the promotion is currently active.
     */
    val isActive: Boolean
        get() {
            val now = Instant.now()
            val start = startTime?.let { runCatching { Instant.parse(it) }.getOrNull() }
            val end = endTime?.let { runCatching { Instant.parse(it) }.getOrNull() }

            if (start != null && now.isBefore(start)) return false
            if (end != null && now.isAfter(end)) return false
            return true
        }

    /**
     * Whether the promotion has a countdown timer.
     */
    val hasCountdown: Boolean
        get() = endTime != null

    /**
     * Get the end time as an Instant, if available.
     */
    fun getEndInstant(): Instant? {
        return endTime?.let { runCatching { Instant.parse(it) }.getOrNull() }
    }
}

/**
 * Configuration for the promo banner.
 */
data class PromoBannerConfig(
    val showCountdown: Boolean = true,
    val dismissible: Boolean = true,
    val position: BannerPosition = BannerPosition.TOP,
    val autoHideSeconds: Int? = null
)

/**
 * Position of the banner.
 */
enum class BannerPosition {
    TOP,
    BOTTOM
}

/**
 * Configuration for the promo modal.
 */
data class PromoModalConfig(
    val showCountdown: Boolean = true,
    val dismissOnBackgroundTap: Boolean = true,
    val showCloseButton: Boolean = true,
    val animateIn: Boolean = true
)

/**
 * Events tracked for promotions.
 */
object PromoEvents {
    const val BANNER_VIEWED = "promo_banner_viewed"
    const val BANNER_CLICKED = "promo_banner_clicked"
    const val BANNER_DISMISSED = "promo_banner_dismissed"
    const val MODAL_VIEWED = "promo_modal_viewed"
    const val MODAL_CTA_CLICKED = "promo_modal_cta_clicked"
    const val MODAL_DISMISSED = "promo_modal_dismissed"
    const val OFFER_CLAIMED = "promo_offer_claimed"
}
