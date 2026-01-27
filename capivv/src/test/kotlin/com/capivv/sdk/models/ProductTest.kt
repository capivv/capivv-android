package com.capivv.sdk.models

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Product model.
 */
class ProductTest {

    @Test
    fun `product with subscription type is subscription`() {
        val product = Product(
            identifier = "premium_monthly",
            title = "Premium Monthly",
            description = "Monthly premium subscription",
            price = 999,
            priceString = "$9.99",
            currencyCode = "USD",
            productType = ProductType.SUBSCRIPTION,
            subscriptionPeriod = "P1M"
        )

        assertEquals(ProductType.SUBSCRIPTION, product.productType)
        assertTrue(product.isSubscription)
        assertEquals("P1M", product.subscriptionPeriod)
    }

    @Test
    fun `product with consumable type has no subscription period`() {
        val product = Product(
            identifier = "coins_100",
            title = "100 Coins",
            description = "100 in-app coins",
            price = 99,
            priceString = "$0.99",
            currencyCode = "USD",
            productType = ProductType.CONSUMABLE
        )

        assertEquals(ProductType.CONSUMABLE, product.productType)
        assertFalse(product.isSubscription)
        assertNull(product.subscriptionPeriod)
    }

    @Test
    fun `product formatted period returns correct value`() {
        val product = Product(
            identifier = "premium_monthly",
            title = "Premium",
            description = "Premium subscription",
            price = 999,
            priceString = "$9.99",
            currencyCode = "USD",
            productType = ProductType.SUBSCRIPTION,
            subscriptionPeriod = "P1M"
        )

        assertEquals("Monthly", product.getFormattedPeriod())
    }

    @Test
    fun `product with trial has free trial`() {
        val product = Product(
            identifier = "premium_trial",
            title = "Premium Trial",
            description = "Premium with trial",
            price = 999,
            priceString = "$9.99",
            currencyCode = "USD",
            productType = ProductType.SUBSCRIPTION,
            subscriptionPeriod = "P1M",
            trialPeriod = "P7D"
        )

        assertTrue(product.hasFreeTrial)
    }
}
