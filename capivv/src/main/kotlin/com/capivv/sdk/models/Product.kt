package com.capivv.sdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Product type enum matching the backend.
 */
@Serializable
enum class ProductType {
    @SerialName("SUBSCRIPTION")
    SUBSCRIPTION,

    @SerialName("CONSUMABLE")
    CONSUMABLE,

    @SerialName("NON_CONSUMABLE")
    NON_CONSUMABLE
}

/**
 * A product available for purchase.
 *
 * @param identifier Unique product identifier (matches store product ID)
 * @param title Display title
 * @param description Product description
 * @param price Price in the smallest currency unit (e.g., cents)
 * @param priceString Formatted price string (e.g., "$9.99")
 * @param currencyCode ISO 4217 currency code (e.g., "USD")
 * @param productType Type of product
 * @param subscriptionPeriod For subscriptions, the billing period (e.g., "P1M" for monthly)
 * @param trialPeriod For subscriptions with trials, the trial period
 * @param introductoryPrice For subscriptions, any introductory price
 */
@Serializable
data class Product(
    val identifier: String,
    val title: String,
    val description: String,
    val price: Long,
    @SerialName("price_string")
    val priceString: String,
    @SerialName("currency_code")
    val currencyCode: String,
    @SerialName("product_type")
    val productType: ProductType,
    @SerialName("subscription_period")
    val subscriptionPeriod: String? = null,
    @SerialName("trial_period")
    val trialPeriod: String? = null,
    @SerialName("introductory_price")
    val introductoryPrice: String? = null
) {
    /**
     * Whether this product is a subscription.
     */
    val isSubscription: Boolean
        get() = productType == ProductType.SUBSCRIPTION

    /**
     * Whether this product has a free trial.
     */
    val hasFreeTrial: Boolean
        get() = !trialPeriod.isNullOrBlank()

    /**
     * Get a human-readable subscription period.
     */
    fun getFormattedPeriod(): String? {
        return subscriptionPeriod?.let { period ->
            when {
                period.contains("P1M") -> "Monthly"
                period.contains("P3M") -> "Quarterly"
                period.contains("P6M") -> "Semi-Annual"
                period.contains("P1Y") -> "Annual"
                period.contains("P1W") -> "Weekly"
                else -> period
            }
        }
    }
}
