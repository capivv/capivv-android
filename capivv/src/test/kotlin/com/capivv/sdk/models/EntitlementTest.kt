package com.capivv.sdk.models

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Entitlement model.
 */
class EntitlementTest {

    @Test
    fun `active entitlement has isActive true`() {
        val entitlement = Entitlement(
            identifier = "premium",
            productIdentifier = "premium_monthly",
            isActive = true
        )

        assertTrue(entitlement.isActive)
    }

    @Test
    fun `expired entitlement has isActive false`() {
        val entitlement = Entitlement(
            identifier = "premium",
            productIdentifier = "premium_monthly",
            isActive = false
        )

        assertFalse(entitlement.isActive)
    }

    @Test
    fun `entitlement check result finds active entitlement`() {
        val result = EntitlementCheckResult(
            entitlements = listOf(
                Entitlement(
                    identifier = "premium",
                    productIdentifier = "premium_monthly",
                    isActive = true
                ),
                Entitlement(
                    identifier = "basic",
                    productIdentifier = "basic_monthly",
                    isActive = false
                )
            )
        )

        assertTrue(result.hasEntitlement("premium"))
        assertFalse(result.hasEntitlement("basic"))
        assertFalse(result.hasEntitlement("nonexistent"))
    }

    @Test
    fun `entitlement check result returns active entitlements`() {
        val result = EntitlementCheckResult(
            entitlements = listOf(
                Entitlement(
                    identifier = "premium",
                    productIdentifier = "premium_monthly",
                    isActive = true
                ),
                Entitlement(
                    identifier = "basic",
                    productIdentifier = "basic_monthly",
                    isActive = false
                )
            )
        )

        val activeEntitlements = result.getActiveEntitlements()
        assertEquals(1, activeEntitlements.size)
        assertEquals("premium", activeEntitlements[0].identifier)
    }

    @Test
    fun `entitlement check result hasAnyActiveEntitlement works`() {
        val withActive = EntitlementCheckResult(
            entitlements = listOf(
                Entitlement(identifier = "premium", productIdentifier = null, isActive = true)
            )
        )

        val withoutActive = EntitlementCheckResult(
            entitlements = listOf(
                Entitlement(identifier = "premium", productIdentifier = null, isActive = false)
            )
        )

        assertTrue(withActive.hasAnyActiveEntitlement())
        assertFalse(withoutActive.hasAnyActiveEntitlement())
    }
}
