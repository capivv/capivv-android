package com.capivv.sdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * State of a purchase.
 */
enum class PurchaseState {
    PURCHASED,
    PENDING,
    CANCELLED,
    FAILED,
    RESTORED
}

/**
 * Result of a purchase attempt.
 *
 * @param success Whether the purchase was successful
 * @param state The state of the purchase
 * @param productIdentifier The product that was purchased
 * @param transactionId The transaction identifier
 * @param entitlements Entitlements granted by the purchase
 * @param error Error message if purchase failed
 */
@Serializable
data class PurchaseResult(
    val success: Boolean,
    val state: PurchaseState = if (success) PurchaseState.PURCHASED else PurchaseState.FAILED,
    @SerialName("product_identifier")
    val productIdentifier: String? = null,
    @SerialName("transaction_id")
    val transactionId: String? = null,
    val entitlements: List<Entitlement>? = null,
    val error: String? = null
) {
    companion object {
        fun success(
            productIdentifier: String,
            transactionId: String,
            entitlements: List<Entitlement> = emptyList()
        ) = PurchaseResult(
            success = true,
            state = PurchaseState.PURCHASED,
            productIdentifier = productIdentifier,
            transactionId = transactionId,
            entitlements = entitlements
        )

        fun cancelled() = PurchaseResult(
            success = false,
            state = PurchaseState.CANCELLED,
            error = "Purchase was cancelled"
        )

        fun pending(productIdentifier: String) = PurchaseResult(
            success = false,
            state = PurchaseState.PENDING,
            productIdentifier = productIdentifier,
            error = "Purchase is pending"
        )

        fun failed(error: String) = PurchaseResult(
            success = false,
            state = PurchaseState.FAILED,
            error = error
        )

        fun restored(entitlements: List<Entitlement>) = PurchaseResult(
            success = true,
            state = PurchaseState.RESTORED,
            entitlements = entitlements
        )
    }
}

/**
 * Result of restoring purchases.
 */
@Serializable
data class RestoreResult(
    val success: Boolean,
    val entitlements: List<Entitlement> = emptyList(),
    val error: String? = null
)
