package com.capivv.sdk.ui.paywall

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.capivv.sdk.Capivv
import com.capivv.sdk.models.Offering
import com.capivv.sdk.models.Product
import com.capivv.sdk.models.PurchaseResult
import kotlinx.coroutines.launch

/**
 * Activity for displaying the Capivv paywall.
 *
 * Launch with:
 * ```kotlin
 * PaywallActivity.launch(
 *     context = this,
 *     offeringId = "default",
 *     config = PaywallConfig(
 *         title = "Go Premium",
 *         features = listOf("Feature 1", "Feature 2")
 *     )
 * )
 * ```
 */
class PaywallActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_OFFERING_ID = "offering_id"
        private const val EXTRA_CONFIG = "config"

        const val RESULT_PURCHASED = Activity.RESULT_FIRST_USER + 1
        const val RESULT_RESTORED = Activity.RESULT_FIRST_USER + 2
        const val RESULT_CANCELLED = Activity.RESULT_CANCELED

        /**
         * Launch the paywall activity.
         *
         * @param context The activity context
         * @param offeringId The offering ID to display (default: "default")
         * @param config Optional paywall configuration
         */
        fun launch(
            context: Context,
            offeringId: String = "default",
            config: PaywallConfig = PaywallConfig()
        ) {
            val intent = Intent(context, PaywallActivity::class.java).apply {
                putExtra(EXTRA_OFFERING_ID, offeringId)
                // Note: For complex config, consider using a ViewModel or singleton
            }
            context.startActivity(intent)
        }

        /**
         * Launch for result to receive purchase outcome.
         */
        fun launchForResult(
            activity: Activity,
            requestCode: Int,
            offeringId: String = "default"
        ) {
            val intent = Intent(activity, PaywallActivity::class.java).apply {
                putExtra(EXTRA_OFFERING_ID, offeringId)
            }
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private var config: PaywallConfig = PaywallConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val offeringId = intent.getStringExtra(EXTRA_OFFERING_ID) ?: "default"

        setContent {
            PaywallContent(
                offeringId = offeringId,
                config = config,
                onPurchaseComplete = { result ->
                    if (result.success) {
                        setResult(RESULT_PURCHASED)
                        finish()
                    }
                },
                onRestoreComplete = { success ->
                    if (success) {
                        setResult(RESULT_RESTORED)
                        finish()
                    }
                },
                onDismiss = {
                    setResult(RESULT_CANCELLED)
                    finish()
                }
            )
        }
    }
}

@Composable
private fun PaywallContent(
    offeringId: String,
    config: PaywallConfig,
    onPurchaseComplete: (PurchaseResult) -> Unit,
    onRestoreComplete: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? Activity

    var offering by remember { mutableStateOf<Offering?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isPurchasing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Load offerings
    LaunchedEffect(offeringId) {
        isLoading = true
        error = null

        Capivv.getOfferings().fold(
            onSuccess = { result ->
                offering = result.offerings.find { it.identifier == offeringId }
                    ?: result.offerings.firstOrNull()

                offering?.products?.firstOrNull()?.let {
                    selectedProduct = it
                }
                isLoading = false
            },
            onFailure = { e ->
                error = e.message ?: "Failed to load products"
                isLoading = false
            }
        )
    }

    when {
        isLoading -> {
            LoadingScreen()
        }
        error != null -> {
            ErrorScreen(
                message = error!!,
                onRetry = { error = null },
                onDismiss = onDismiss
            )
        }
        offering != null -> {
            PaywallScreen(
                offering = offering!!,
                config = config,
                isLoading = isPurchasing,
                selectedProduct = selectedProduct,
                onProductSelected = { selectedProduct = it },
                onPurchase = { product ->
                    activity?.let { act ->
                        isPurchasing = true
                        (act as ComponentActivity).lifecycleScope.launch {
                            Capivv.purchase(act, product).fold(
                                onSuccess = { result ->
                                    isPurchasing = false
                                    onPurchaseComplete(result)
                                },
                                onFailure = { e ->
                                    isPurchasing = false
                                    Toast.makeText(
                                        act,
                                        e.message ?: "Purchase failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                },
                onRestore = {
                    activity?.let { act ->
                        isPurchasing = true
                        (act as ComponentActivity).lifecycleScope.launch {
                            Capivv.restorePurchases().fold(
                                onSuccess = { result ->
                                    isPurchasing = false
                                    onRestoreComplete(result.success)
                                    if (!result.success) {
                                        Toast.makeText(
                                            act,
                                            result.error ?: "No purchases to restore",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onFailure = { e ->
                                    isPurchasing = false
                                    Toast.makeText(
                                        act,
                                        e.message ?: "Restore failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                },
                onDismiss = onDismiss,
                onTermsClick = config.termsUrl?.let { url ->
                    {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        )
                    }
                },
                onPrivacyClick = config.privacyUrl?.let { url ->
                    {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun LoadingScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        androidx.compose.material3.Text(
            text = message,
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        androidx.compose.foundation.layout.Spacer(
            modifier = androidx.compose.ui.Modifier.height(24.dp)
        )
        androidx.compose.material3.Button(onClick = onRetry) {
            androidx.compose.material3.Text("Retry")
        }
        androidx.compose.foundation.layout.Spacer(
            modifier = androidx.compose.ui.Modifier.height(12.dp)
        )
        androidx.compose.material3.TextButton(onClick = onDismiss) {
            androidx.compose.material3.Text("Cancel")
        }
    }
}

