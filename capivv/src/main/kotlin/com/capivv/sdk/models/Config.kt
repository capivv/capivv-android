package com.capivv.sdk.models

/**
 * Configuration for the Capivv SDK.
 *
 * @param apiKey Your Capivv API key (starts with "capivv_secret_")
 * @param baseUrl The Capivv API base URL (defaults to production)
 * @param debug Enable debug logging
 * @param cacheEnabled Enable response caching
 * @param cacheTtlSeconds Cache time-to-live in seconds
 */
data class CapivvConfig(
    val apiKey: String,
    val baseUrl: String = "https://api.capivv.com",
    val debug: Boolean = false,
    val cacheEnabled: Boolean = true,
    val cacheTtlSeconds: Long = 300L
) {
    init {
        require(apiKey.isNotBlank()) { "API key cannot be blank" }
        require(apiKey.startsWith("capivv_")) { "Invalid API key format" }
    }

    companion object {
        /**
         * Create a configuration with the default production settings.
         */
        fun production(apiKey: String) = CapivvConfig(
            apiKey = apiKey,
            baseUrl = "https://api.capivv.com",
            debug = false
        )

        /**
         * Create a configuration for development/testing.
         */
        fun development(apiKey: String, baseUrl: String) = CapivvConfig(
            apiKey = apiKey,
            baseUrl = baseUrl,
            debug = true
        )
    }
}
