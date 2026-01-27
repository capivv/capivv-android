package com.capivv.sdk.ui.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capivv.sdk.models.Offering
import com.capivv.sdk.models.Product
import com.capivv.sdk.ui.components.FeatureList
import com.capivv.sdk.ui.components.ProductCard
import com.capivv.sdk.ui.theme.CapivvTheme
import com.capivv.sdk.ui.theme.CapivvThemeConfig

/**
 * Configuration for the paywall screen.
 */
data class PaywallConfig(
    val title: String = "Unlock Premium",
    val subtitle: String? = null,
    val features: List<String> = emptyList(),
    val ctaText: String = "Subscribe Now",
    val restoreText: String = "Restore Purchases",
    val showCloseButton: Boolean = true,
    val showRestoreButton: Boolean = true,
    val termsUrl: String? = null,
    val privacyUrl: String? = null,
    val themeConfig: CapivvThemeConfig = CapivvThemeConfig()
)

/**
 * The main paywall screen composable.
 *
 * @param offering The offering to display
 * @param config Configuration for the paywall
 * @param isLoading Whether a purchase is in progress
 * @param onProductSelected Called when a product is selected
 * @param onPurchase Called when the purchase button is pressed
 * @param onRestore Called when restore is pressed
 * @param onDismiss Called when the close button is pressed
 * @param onTermsClick Called when terms link is clicked
 * @param onPrivacyClick Called when privacy link is clicked
 */
@Composable
fun PaywallScreen(
    offering: Offering,
    config: PaywallConfig = PaywallConfig(),
    isLoading: Boolean = false,
    selectedProduct: Product? = null,
    onProductSelected: (Product) -> Unit,
    onPurchase: (Product) -> Unit,
    onRestore: () -> Unit,
    onDismiss: () -> Unit,
    onTermsClick: (() -> Unit)? = null,
    onPrivacyClick: (() -> Unit)? = null
) {
    CapivvTheme(config = config.themeConfig) {
        Scaffold(
            topBar = {
                if (config.showCloseButton) {
                    PaywallTopBar(onDismiss = onDismiss)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                PaywallHeader(
                    title = config.title,
                    subtitle = config.subtitle
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Features
                if (config.features.isNotEmpty()) {
                    FeatureList(features = config.features)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Products
                ProductList(
                    products = offering.products,
                    selectedProduct = selectedProduct,
                    onProductSelected = onProductSelected
                )

                Spacer(modifier = Modifier.height(24.dp))

                // CTA Button
                PurchaseButton(
                    text = config.ctaText,
                    isLoading = isLoading,
                    enabled = selectedProduct != null && !isLoading,
                    onClick = { selectedProduct?.let(onPurchase) }
                )

                // Restore button
                if (config.showRestoreButton) {
                    Spacer(modifier = Modifier.height(12.dp))
                    RestoreButton(
                        text = config.restoreText,
                        onClick = onRestore,
                        enabled = !isLoading
                    )
                }

                // Footer
                Spacer(modifier = Modifier.height(16.dp))
                PaywallFooter(
                    selectedProduct = selectedProduct,
                    termsUrl = config.termsUrl,
                    privacyUrl = config.privacyUrl,
                    onTermsClick = onTermsClick,
                    onPrivacyClick = onPrivacyClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaywallTopBar(onDismiss: () -> Unit) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun PaywallHeader(
    title: String,
    subtitle: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProductList(
    products: List<Product>,
    selectedProduct: Product?,
    onProductSelected: (Product) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        products.forEach { product ->
            ProductCard(
                product = product,
                isSelected = product.identifier == selectedProduct?.identifier,
                onClick = { onProductSelected(product) }
            )
        }
    }
}

@Composable
private fun PurchaseButton(
    text: String,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.large
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RestoreButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PaywallFooter(
    selectedProduct: Product?,
    termsUrl: String?,
    privacyUrl: String?,
    onTermsClick: (() -> Unit)?,
    onPrivacyClick: (() -> Unit)?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Links
        if (termsUrl != null || privacyUrl != null) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                if (termsUrl != null) {
                    TextButton(onClick = { onTermsClick?.invoke() }) {
                        Text(
                            text = "Terms of Service",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (termsUrl != null && privacyUrl != null) {
                    Text(
                        text = " • ",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                if (privacyUrl != null) {
                    TextButton(onClick = { onPrivacyClick?.invoke() }) {
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Subscription disclaimer
        if (selectedProduct?.isSubscription == true) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Subscription automatically renews unless cancelled at least 24 hours before the end of the current period.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}
