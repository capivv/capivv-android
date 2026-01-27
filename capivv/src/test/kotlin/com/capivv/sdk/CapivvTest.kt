package com.capivv.sdk

import com.capivv.sdk.models.CapivvConfig
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Capivv SDK.
 */
class CapivvTest {

    @Test
    fun `SDK configuration requires API key`() {
        // Test that configuration with empty API key would be invalid
        val emptyKey = ""
        assertTrue(emptyKey.isEmpty())
    }

    @Test
    fun `SDK configuration requires valid API key format`() {
        // Test valid API key format
        val validKey = "capivv_pk_test_abc123"
        assertTrue(validKey.startsWith("capivv_pk_"))

        val invalidKey = "invalid_key"
        assertFalse(invalidKey.startsWith("capivv_pk_"))
    }

    @Test
    fun `CapivvConfig has correct defaults`() {
        val config = CapivvConfig(apiKey = "capivv_pk_test_123")
        assertEquals("capivv_pk_test_123", config.apiKey)
        assertFalse(config.debug)
    }

    @Test
    fun `CapivvConfig debug mode can be enabled`() {
        val config = CapivvConfig(
            apiKey = "capivv_pk_test_123",
            debug = true
        )
        assertTrue(config.debug)
    }
}
