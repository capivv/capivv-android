package com.capivv.sdk

import android.app.Activity
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capivv.sdk.api.CapivvApi
import com.capivv.sdk.billing.BillingManager
import com.capivv.sdk.models.*
import com.capivv.sdk.templates.TemplateDefinition
import com.capivv.sdk.templates.TemplateLoadResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Context.capivvDataStore: DataStore<Preferences> by preferencesDataStore(name = "capivv_prefs")

/**
 * Main entry point for the Capivv SDK.
 *
 * Initialize the SDK in your Application class:
 * ```kotlin
 * class MyApp : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         Capivv.configure(this, CapivvConfig(apiKey = "capivv_secret_..."))
 *     }
 * }
 * ```
 */
object Capivv {
    private val TAG = "Capivv"

    private var _config: CapivvConfig? = null
    private var _api: CapivvApi? = null
    private var _billingManager: BillingManager? = null
    private var _context: Context? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // State flows for reactive updates
    private val _currentUserId = MutableStateFlow<String?>(null)
    private val _entitlements = MutableStateFlow<List<Entitlement>>(emptyList())
    private val _isConfigured = MutableStateFlow(false)

    /**
     * The current user ID, if identified.
     */
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    /**
     * The current user's entitlements.
     */
    val entitlements: StateFlow<List<Entitlement>> = _entitlements.asStateFlow()

    /**
     * Whether the SDK is configured.
     */
    val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

    // DataStore keys
    private val USER_ID_KEY = stringPreferencesKey("capivv_user_id")

    /**
     * Configure the Capivv SDK. Call this once in your Application.onCreate().
     *
     * @param context Application context
     * @param config SDK configuration
     */
    fun configure(context: Context, config: CapivvConfig) {
        if (_isConfigured.value) {
            log("SDK already configured, ignoring duplicate configure call")
            return
        }

        _context = context.applicationContext
        _config = config
        _api = CapivvApi(config)
        _billingManager = BillingManager(context.applicationContext, config.debug)
        _isConfigured.value = true

        log("SDK configured with baseUrl: ${config.baseUrl}")

        // Restore previous user session
        scope.launch {
            restoreUserSession()
        }
    }

    /**
     * Identify a user with Capivv.
     *
     * @param userId Unique user identifier
     * @param email Optional email address
     * @param attributes Optional custom attributes
     * @return Result containing the identify response
     */
    suspend fun identify(
        userId: String,
        email: String? = null,
        attributes: Map<String, String>? = null
    ): Result<IdentifyResult> = withContext(Dispatchers.IO) {
        ensureConfigured()

        val userInfo = UserInfo(
            userId = userId,
            email = email,
            attributes = attributes
        )

        val result = api.identify(userInfo)

        result.onSuccess { identifyResult ->
            _currentUserId.value = userId
            _entitlements.value = identifyResult.entitlements
            saveUserId(userId)
            log("User identified: $userId")
        }

        result.onFailure { error ->
            log("Failed to identify user: ${error.message}")
        }

        result
    }

    /**
     * Log out the current user.
     */
    suspend fun logout() {
        _currentUserId.value = null
        _entitlements.value = emptyList()
        clearUserId()
        log("User logged out")
    }

    /**
     * Get available offerings.
     *
     * @return Result containing offerings
     */
    suspend fun getOfferings(): Result<OfferingsResult> = withContext(Dispatchers.IO) {
        ensureConfigured()
        api.getOfferings()
    }

    /**
     * Get a paywall template by identifier for OTA updates.
     *
     * @param identifier The template identifier
     * @return Result containing the template load result
     */
    suspend fun getPaywallTemplate(identifier: String): Result<TemplateLoadResult> = withContext(Dispatchers.IO) {
        ensureConfigured()

        api.getPaywallTemplate(identifier).also { result ->
            result.onFailure { error ->
                log("Failed to load template for $identifier: ${error.message}")
            }
        }
    }

    /**
     * Get offerings and template in parallel for a paywall.
     *
     * @param offeringId The offering ID to fetch
     * @param templateIdentifier The template identifier
     * @return Pair of offerings and template (template may be null)
     */
    suspend fun getPaywallWithTemplate(
        offeringId: String = "default",
        templateIdentifier: String
    ): Pair<Offering?, TemplateDefinition?> = withContext(Dispatchers.IO) {
        ensureConfigured()

        val offeringsDeferred = async { api.getOfferings() }
        val templateDeferred = async { api.getPaywallTemplate(templateIdentifier) }

        val offerings = offeringsDeferred.await().getOrNull()
        val templateResult = templateDeferred.await().getOrNull()

        val offering = offerings?.offerings?.find { it.identifier == offeringId }
            ?: offerings?.offerings?.firstOrNull()

        Pair(offering, templateResult?.template)
    }

    /**
     * Get the current user's entitlements.
     *
     * @return Result containing entitlements
     */
    suspend fun getEntitlements(): Result<EntitlementCheckResult> = withContext(Dispatchers.IO) {
        ensureConfigured()
        ensureIdentified()

        val result = api.getEntitlements(currentUserId.value!!)

        result.onSuccess { checkResult ->
            _entitlements.value = checkResult.entitlements
        }

        result
    }

    /**
     * Check if the user has a specific entitlement.
     *
     * @param identifier The entitlement identifier
     * @return true if the user has the active entitlement
     */
    fun hasEntitlement(identifier: String): Boolean {
        return _entitlements.value.any { it.identifier == identifier && it.isActive }
    }

    /**
     * Purchase a product.
     *
     * @param activity The activity to launch the purchase flow from
     * @param product The product to purchase
     * @return Result containing the purchase result
     */
    suspend fun purchase(
        activity: Activity,
        product: Product
    ): Result<PurchaseResult> = withContext(Dispatchers.IO) {
        ensureConfigured()
        ensureIdentified()

        val userId = currentUserId.value!!

        // Launch billing flow
        val billingResult = billingManager.purchase(activity, product)

        billingResult.fold(
            onSuccess = { purchaseData ->
                // Verify with backend
                api.verifyGooglePurchase(
                    userId = userId,
                    productId = product.identifier,
                    purchaseToken = purchaseData.purchaseToken,
                    orderId = purchaseData.orderId
                ).also { verifyResult ->
                    verifyResult.onSuccess { result ->
                        result.entitlements?.let { newEntitlements ->
                            _entitlements.value = newEntitlements
                        }
                    }
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    /**
     * Restore previous purchases.
     *
     * @return Result containing the restore result
     */
    suspend fun restorePurchases(): Result<RestoreResult> = withContext(Dispatchers.IO) {
        ensureConfigured()
        ensureIdentified()

        val userId = currentUserId.value!!

        // Get purchase history from Play Store
        val purchaseTokens = billingManager.getPurchaseHistory()
            .getOrElse { emptyList() }
            .map { it.purchaseToken }

        if (purchaseTokens.isEmpty()) {
            return@withContext Result.success(
                RestoreResult(
                    success = true,
                    entitlements = emptyList()
                )
            )
        }

        // Send to backend for verification
        val result = api.restorePurchases(userId, purchaseTokens)

        result.onSuccess { restoreResult ->
            _entitlements.value = restoreResult.entitlements
        }

        result
    }

    /**
     * Open the Play Store subscription management page.
     *
     * @param activity The activity context
     */
    fun manageSubscriptions(activity: Activity) {
        billingManager.openSubscriptionManagement(activity)
    }

    /**
     * Track a custom event.
     *
     * @param eventName Name of the event
     * @param properties Additional event properties
     */
    suspend fun trackEvent(
        eventName: String,
        properties: Map<String, Any>? = null
    ) = withContext(Dispatchers.IO) {
        ensureConfigured()

        val userId = currentUserId.value ?: "anonymous"
        api.trackEvent(userId, eventName, properties)
    }

    // Internal helpers

    private val config: CapivvConfig
        get() = _config ?: throw IllegalStateException("Capivv not configured. Call configure() first.")

    private val api: CapivvApi
        get() = _api ?: throw IllegalStateException("Capivv not configured. Call configure() first.")

    private val billingManager: BillingManager
        get() = _billingManager ?: throw IllegalStateException("Capivv not configured. Call configure() first.")

    private val context: Context
        get() = _context ?: throw IllegalStateException("Capivv not configured. Call configure() first.")

    private fun ensureConfigured() {
        if (!_isConfigured.value) {
            throw IllegalStateException("Capivv not configured. Call configure() first.")
        }
    }

    private fun ensureIdentified() {
        if (_currentUserId.value == null) {
            throw IllegalStateException("User not identified. Call identify() first.")
        }
    }

    private suspend fun restoreUserSession() {
        context.capivvDataStore.data.map { prefs ->
            prefs[USER_ID_KEY]
        }.first()?.let { savedUserId ->
            log("Restoring user session for: $savedUserId")
            _currentUserId.value = savedUserId

            // Refresh entitlements
            api.getEntitlements(savedUserId).onSuccess { result ->
                _entitlements.value = result.entitlements
            }
        }
    }

    private suspend fun saveUserId(userId: String) {
        context.capivvDataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    private suspend fun clearUserId() {
        context.capivvDataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
        }
    }

    private fun log(message: String) {
        if (_config?.debug == true) {
            android.util.Log.d(TAG, message)
        }
    }
}
