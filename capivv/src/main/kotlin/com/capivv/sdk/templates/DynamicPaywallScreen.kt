package com.capivv.sdk.templates

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.capivv.sdk.models.Offering
import com.capivv.sdk.models.Product
import com.capivv.sdk.ui.theme.CapivvTheme
import com.capivv.sdk.ui.theme.CapivvThemeConfig
import java.util.Locale

/**
 * A dynamic paywall screen that renders from a TemplateDefinition.
 *
 * @param template The template definition to render
 * @param offering The offering with products to display
 * @param selectedProduct The currently selected product
 * @param isLoading Whether a purchase is in progress
 * @param locale The locale for translations
 * @param themeConfig Optional theme configuration
 * @param onProductSelected Called when a product is selected
 * @param onPurchase Called when purchase is requested
 * @param onRestore Called when restore is requested
 * @param onDismiss Called when the paywall is dismissed
 * @param onUrlClick Called when a URL link is clicked
 */
@Composable
fun DynamicPaywallScreen(
    template: TemplateDefinition,
    offering: Offering?,
    selectedProduct: Product?,
    isLoading: Boolean = false,
    locale: String = Locale.getDefault().language,
    themeConfig: CapivvThemeConfig = CapivvThemeConfig(),
    onProductSelected: (Product) -> Unit,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    onDismiss: () -> Unit,
    onUrlClick: (String) -> Unit = {}
) {
    CapivvTheme(config = themeConfig) {
        Scaffold(
            topBar = {
                if (template.settings.showCloseButton) {
                    DynamicPaywallTopBar(
                        style = template.settings.closeButtonStyle,
                        onDismiss = onDismiss
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .then(getBackgroundModifier(template.background))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    template.components.forEach { component ->
                        ComponentFactory.RenderComponent(
                            component = component,
                            offering = offering,
                            selectedProduct = selectedProduct,
                            locale = locale,
                            onProductSelected = onProductSelected,
                            onPurchase = onPurchase,
                            onRestore = onRestore,
                            onDismiss = onDismiss,
                            onUrlClick = onUrlClick
                        )
                    }
                }

                // Loading overlay
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DynamicPaywallTopBar(
    style: String,
    onDismiss: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            when (style) {
                "text" -> TextButton(onClick = onDismiss) {
                    Text("Close")
                }
                else -> IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

private fun getBackgroundModifier(background: BackgroundStyle?): Modifier {
    if (background == null) return Modifier

    return when (background.type) {
        "gradient" -> {
            val colors = background.gradientColors?.mapNotNull { colorString ->
                try {
                    Color(android.graphics.Color.parseColor(
                        if (colorString.startsWith("#")) colorString else "#$colorString"
                    ))
                } catch (e: Exception) {
                    null
                }
            } ?: listOf(Color.White, Color.LightGray)

            Modifier.background(
                Brush.verticalGradient(colors = colors)
            )
        }
        "solid" -> {
            background.color?.let { colorString ->
                try {
                    val color = Color(android.graphics.Color.parseColor(
                        if (colorString.startsWith("#")) colorString else "#$colorString"
                    ))
                    Modifier.background(color)
                } catch (e: Exception) {
                    Modifier
                }
            } ?: Modifier
        }
        else -> Modifier
    }
}

/**
 * A helper composable that loads a template and displays it.
 */
@Composable
fun DynamicPaywall(
    templateIdentifier: String,
    offeringId: String = "default",
    locale: String = Locale.getDefault().language,
    onPurchaseComplete: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var template by remember { mutableStateOf<TemplateDefinition?>(null) }
    var offering by remember { mutableStateOf<Offering?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isPurchasing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Load template and offerings
    LaunchedEffect(templateIdentifier, offeringId) {
        isLoading = true
        error = null

        try {
            // Load in parallel
            val templateResult = com.capivv.sdk.Capivv.getPaywallTemplate(templateIdentifier)
            val offeringsResult = com.capivv.sdk.Capivv.getOfferings()

            templateResult.onSuccess { result ->
                template = result.template
            }

            offeringsResult.onSuccess { result ->
                offering = result.offerings.find { it.identifier == offeringId }
                    ?: result.offerings.firstOrNull()
                offering?.products?.firstOrNull()?.let {
                    selectedProduct = it
                }
            }

            if (template == null) {
                error = "Template not found"
            }
        } catch (e: Exception) {
            error = e.message ?: "Failed to load"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> LoadingView()
        error != null -> ErrorView(error!!, onDismiss)
        template != null -> DynamicPaywallScreen(
            template = template!!,
            offering = offering,
            selectedProduct = selectedProduct,
            isLoading = isPurchasing,
            locale = locale,
            onProductSelected = { selectedProduct = it },
            onPurchase = {
                // Purchase handling would go here
                isPurchasing = true
            },
            onRestore = {
                // Restore handling would go here
            },
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(message: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onDismiss) {
            Text("Close")
        }
    }
}
