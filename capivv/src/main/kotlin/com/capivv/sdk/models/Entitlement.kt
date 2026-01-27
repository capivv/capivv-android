package com.capivv.sdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An entitlement granted to a user.
 *
 * @param identifier Unique entitlement identifier
 * @param productIdentifier The product that granted this entitlement
 * @param isActive Whether the entitlement is currently active
 * @param willRenew Whether the subscription will auto-renew
 * @param expiresAt When the entitlement expires (null if non-expiring)
 * @param purchasedAt When the entitlement was originally purchased
 */
@Serializable
data class Entitlement(
    val identifier: String,
    @SerialName("product_identifier")
    val productIdentifier: String?,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("will_renew")
    val willRenew: Boolean = false,
    @SerialName("expires_at")
    val expiresAt: String? = null,
    @SerialName("purchased_at")
    val purchasedAt: String? = null
)

/**
 * Result of checking entitlements.
 */
@Serializable
data class EntitlementCheckResult(
    val entitlements: List<Entitlement>,
    @SerialName("checked_at")
    val checkedAt: String? = null
) {
    /**
     * Check if the user has a specific entitlement.
     */
    fun hasEntitlement(identifier: String): Boolean {
        return entitlements.any { it.identifier == identifier && it.isActive }
    }

    /**
     * Get all active entitlements.
     */
    fun getActiveEntitlements(): List<Entitlement> {
        return entitlements.filter { it.isActive }
    }

    /**
     * Check if the user has any active entitlements.
     */
    fun hasAnyActiveEntitlement(): Boolean {
        return entitlements.any { it.isActive }
    }
}
