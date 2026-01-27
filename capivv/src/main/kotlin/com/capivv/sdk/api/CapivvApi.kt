package com.capivv.sdk.api

import com.capivv.sdk.models.*
import com.capivv.sdk.templates.TemplateLoadResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

/**
 * API client for communicating with the Capivv backend.
 */
internal class CapivvApi(
    private val config: CapivvConfig
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    private val client = HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
            }
        }

        install(ContentNegotiation) {
            json(json)
        }

        if (config.debug) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        android.util.Log.d("CapivvApi", message)
                    }
                }
                level = LogLevel.BODY
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }

        defaultRequest {
            url(config.baseUrl)
            header("Authorization", "Bearer ${config.apiKey}")
            header("Content-Type", "application/json")
            header("X-Capivv-Platform", "android")
            header("X-Capivv-SDK-Version", com.capivv.sdk.BuildConfig.SDK_VERSION)
        }
    }

    /**
     * Identify a user with the Capivv backend.
     */
    suspend fun identify(userInfo: UserInfo): Result<IdentifyResult> = runCatching {
        val response = client.post("/v1/users/${userInfo.userId}/login") {
            setBody(userInfo)
        }
        checkResponse(response)
        response.body<IdentifyResult>()
    }

    /**
     * Get offerings for the current user.
     */
    suspend fun getOfferings(): Result<OfferingsResult> = runCatching {
        val response = client.get("/v1/offerings")
        checkResponse(response)
        response.body<OfferingsResult>()
    }

    /**
     * Get entitlements for a user.
     */
    suspend fun getEntitlements(userId: String): Result<EntitlementCheckResult> = runCatching {
        val response = client.get("/v1/users/$userId/entitlements")
        checkResponse(response)
        response.body<EntitlementCheckResult>()
    }

    /**
     * Verify a Google Play purchase with the backend.
     */
    suspend fun verifyGooglePurchase(
        userId: String,
        productId: String,
        purchaseToken: String,
        orderId: String?
    ): Result<PurchaseResult> = runCatching {
        val response = client.post("/v1/purchases/google/verify") {
            setBody(mapOf(
                "user_id" to userId,
                "product_id" to productId,
                "purchase_token" to purchaseToken,
                "order_id" to orderId
            ))
        }
        checkResponse(response)
        response.body<PurchaseResult>()
    }

    /**
     * Restore purchases for a user.
     */
    suspend fun restorePurchases(
        userId: String,
        purchaseTokens: List<String>
    ): Result<RestoreResult> = runCatching {
        val response = client.post("/v1/users/$userId/restore") {
            setBody(mapOf(
                "platform" to "android",
                "purchase_tokens" to purchaseTokens
            ))
        }
        checkResponse(response)
        response.body<RestoreResult>()
    }

    /**
     * Track an analytics event.
     */
    suspend fun trackEvent(
        userId: String,
        eventName: String,
        properties: Map<String, Any>? = null
    ): Result<Unit> = runCatching {
        val response = client.post("/v1/events") {
            setBody(mapOf(
                "user_id" to userId,
                "event" to eventName,
                "properties" to properties,
                "platform" to "android",
                "timestamp" to System.currentTimeMillis()
            ))
        }
        checkResponse(response)
    }

    /**
     * Get a paywall template by identifier for OTA updates.
     */
    suspend fun getPaywallTemplate(identifier: String): Result<TemplateLoadResult> = runCatching {
        val response = client.get("/v1/paywalls/by-identifier/$identifier/template")
        checkResponse(response)
        response.body<TemplateLoadResult>()
    }

    private suspend fun checkResponse(response: HttpResponse) {
        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw CapivvApiException(
                statusCode = response.status.value,
                message = "API error: ${response.status.description}",
                body = errorBody
            )
        }
    }

    /**
     * Close the HTTP client.
     */
    fun close() {
        client.close()
    }
}

/**
 * Exception thrown when API calls fail.
 */
class CapivvApiException(
    val statusCode: Int,
    override val message: String,
    val body: String? = null
) : Exception(message)
