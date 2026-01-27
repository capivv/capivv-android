package com.capivv.sdk.billing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.android.billingclient.api.*
import com.capivv.sdk.models.Product
import com.capivv.sdk.models.ProductType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Data class for purchase results from Google Play.
 */
data class PurchaseData(
    val purchaseToken: String,
    val orderId: String?,
    val productId: String,
    val purchaseState: Int,
    val isAcknowledged: Boolean
)

/**
 * Manages Google Play Billing interactions.
 */
internal class BillingManager(
    private val context: Context,
    private val debug: Boolean = false
) : PurchasesUpdatedListener {

    private val TAG = "BillingManager"

    private var billingClient: BillingClient? = null
    private var pendingPurchaseDeferred: CompletableDeferred<Result<PurchaseData>>? = null

    private val purchasesUpdatedListener = this

    init {
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    /**
     * Ensure connection to Google Play Billing.
     */
    private suspend fun ensureConnected(): BillingClient = suspendCancellableCoroutine { cont ->
        val client = billingClient ?: run {
            initializeBillingClient()
            billingClient!!
        }

        if (client.isReady) {
            cont.resume(client)
            return@suspendCancellableCoroutine
        }

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    log("Billing client connected")
                    cont.resume(client)
                } else {
                    val error = BillingException(
                        billingResult.responseCode,
                        "Failed to connect to billing: ${billingResult.debugMessage}"
                    )
                    cont.resumeWithException(error)
                }
            }

            override fun onBillingServiceDisconnected() {
                log("Billing service disconnected")
                // Retry connection on next call
            }
        })
    }

    /**
     * Query product details from Google Play.
     */
    suspend fun queryProducts(
        productIds: List<String>,
        productType: ProductType
    ): Result<List<ProductDetails>> = withContext(Dispatchers.IO) {
        runCatching {
            val client = ensureConnected()

            val googleProductType = when (productType) {
                ProductType.SUBSCRIPTION -> BillingClient.ProductType.SUBS
                else -> BillingClient.ProductType.INAPP
            }

            val productList = productIds.map { productId ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(googleProductType)
                    .build()
            }

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            suspendCancellableCoroutine { cont ->
                client.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        cont.resume(productDetailsList)
                    } else {
                        cont.resumeWithException(
                            BillingException(
                                billingResult.responseCode,
                                "Failed to query products: ${billingResult.debugMessage}"
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * Launch a purchase flow.
     */
    suspend fun purchase(
        activity: Activity,
        product: Product
    ): Result<PurchaseData> = withContext(Dispatchers.Main) {
        runCatching {
            val client = ensureConnected()

            // Query product details first
            val productDetails = queryProducts(
                listOf(product.identifier),
                product.productType
            ).getOrThrow().firstOrNull()
                ?: throw BillingException(-1, "Product not found: ${product.identifier}")

            // Build purchase params
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)

            // For subscriptions, select the first offer
            if (product.isSubscription) {
                productDetails.subscriptionOfferDetails?.firstOrNull()?.let { offer ->
                    productDetailsParams.setOfferToken(offer.offerToken)
                }
            }

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams.build()))
                .build()

            // Create deferred for result
            pendingPurchaseDeferred = CompletableDeferred()

            // Launch billing flow
            val result = client.launchBillingFlow(activity, billingFlowParams)

            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                pendingPurchaseDeferred = null
                throw BillingException(
                    result.responseCode,
                    "Failed to launch billing flow: ${result.debugMessage}"
                )
            }

            // Wait for purchase result
            pendingPurchaseDeferred!!.await().getOrThrow()
        }
    }

    /**
     * Handle purchase updates from Google Play.
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.firstOrNull()?.let { purchase ->
                    handlePurchase(purchase)
                } ?: run {
                    pendingPurchaseDeferred?.complete(
                        Result.failure(BillingException(-1, "No purchase returned"))
                    )
                    pendingPurchaseDeferred = null
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                log("User cancelled purchase")
                pendingPurchaseDeferred?.complete(
                    Result.failure(BillingException(
                        billingResult.responseCode,
                        "Purchase cancelled by user"
                    ))
                )
                pendingPurchaseDeferred = null
            }
            else -> {
                log("Purchase failed: ${billingResult.debugMessage}")
                pendingPurchaseDeferred?.complete(
                    Result.failure(BillingException(
                        billingResult.responseCode,
                        "Purchase failed: ${billingResult.debugMessage}"
                    ))
                )
                pendingPurchaseDeferred = null
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        log("Purchase successful: ${purchase.products.firstOrNull()}")

        val purchaseData = PurchaseData(
            purchaseToken = purchase.purchaseToken,
            orderId = purchase.orderId,
            productId = purchase.products.firstOrNull() ?: "",
            purchaseState = purchase.purchaseState,
            isAcknowledged = purchase.isAcknowledged
        )

        // Acknowledge the purchase if needed
        if (!purchase.isAcknowledged && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            acknowledgePurchase(purchase)
        }

        pendingPurchaseDeferred?.complete(Result.success(purchaseData))
        pendingPurchaseDeferred = null
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val client = billingClient ?: return

        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        client.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                log("Purchase acknowledged")
            } else {
                log("Failed to acknowledge purchase: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Get purchase history for restoring purchases.
     */
    suspend fun getPurchaseHistory(): Result<List<PurchaseHistoryRecord>> = withContext(Dispatchers.IO) {
        runCatching {
            val client = ensureConnected()

            val allRecords = mutableListOf<PurchaseHistoryRecord>()

            // Query subscriptions
            val subsParams = QueryPurchaseHistoryParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            val subsRecords = queryPurchaseHistoryAsync(client, subsParams)
            allRecords.addAll(subsRecords)

            // Query in-app purchases
            val inAppParams = QueryPurchaseHistoryParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            val inAppRecords = queryPurchaseHistoryAsync(client, inAppParams)
            allRecords.addAll(inAppRecords)

            allRecords
        }
    }

    private suspend fun queryPurchaseHistoryAsync(
        client: BillingClient,
        params: QueryPurchaseHistoryParams
    ): List<PurchaseHistoryRecord> = suspendCancellableCoroutine { cont ->
        client.queryPurchaseHistoryAsync(params) { billingResult, purchaseHistoryRecordList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                cont.resume(purchaseHistoryRecordList ?: emptyList())
            } else {
                cont.resume(emptyList())
            }
        }
    }

    /**
     * Get active purchases.
     */
    suspend fun getActivePurchases(): Result<List<Purchase>> = withContext(Dispatchers.IO) {
        runCatching {
            val client = ensureConnected()

            val allPurchases = mutableListOf<Purchase>()

            // Query active subscriptions
            val subsParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            client.queryPurchasesAsync(subsParams) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    allPurchases.addAll(purchases)
                }
            }

            // Query active in-app purchases
            val inAppParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            client.queryPurchasesAsync(inAppParams) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    allPurchases.addAll(purchases)
                }
            }

            allPurchases
        }
    }

    /**
     * Open Google Play subscription management.
     */
    fun openSubscriptionManagement(activity: Activity) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/account/subscriptions")
            setPackage("com.android.vending")
        }

        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            // Fallback to browser
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/account/subscriptions")
            }
            activity.startActivity(browserIntent)
        }
    }

    /**
     * Disconnect from billing client.
     */
    fun disconnect() {
        billingClient?.endConnection()
        billingClient = null
    }

    private fun log(message: String) {
        if (debug) {
            Log.d(TAG, message)
        }
    }
}

/**
 * Exception for billing errors.
 */
class BillingException(
    val responseCode: Int,
    override val message: String
) : Exception(message) {

    val isUserCancelled: Boolean
        get() = responseCode == BillingClient.BillingResponseCode.USER_CANCELED

    val isItemAlreadyOwned: Boolean
        get() = responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
}
