package com.capivv.sdk.models

import kotlinx.serialization.Serializable

/**
 * An offering containing a group of products.
 *
 * @param identifier Unique offering identifier
 * @param description Optional description
 * @param products Products in this offering
 * @param metadata Additional metadata
 */
@Serializable
data class Offering(
    val identifier: String,
    val description: String? = null,
    val products: List<Product>,
    val metadata: Map<String, String>? = null
) {
    /**
     * Get the first/primary product in this offering.
     */
    val primaryProduct: Product?
        get() = products.firstOrNull()

    /**
     * Get all subscription products.
     */
    fun getSubscriptions(): List<Product> {
        return products.filter { it.isSubscription }
    }

    /**
     * Get a product by identifier.
     */
    fun getProduct(identifier: String): Product? {
        return products.find { it.identifier == identifier }
    }
}

/**
 * Result of fetching offerings.
 */
@Serializable
data class OfferingsResult(
    val offerings: List<Offering>,
    val current: Offering? = null
)
