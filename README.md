# Capivv Android SDK

The official Capivv SDK for Android. Manage in-app subscriptions and entitlements with ease.

## Features

- 🛒 **Google Play Billing** - Full integration with Play Billing Library 6.x
- 🎨 **Jetpack Compose UI** - Beautiful, customizable paywall components
- 🔄 **Dynamic Templates** - OTA template updates without app releases
- 🌍 **Localization** - 8 languages supported out of the box
- 📊 **Analytics** - Built-in event tracking
- 🔐 **Secure** - EncryptedSharedPreferences for credential storage

## Installation

Add the dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.capivv:capivv-sdk:1.0.0")
}
```

## Quick Start

### 1. Initialize the SDK

In your `Application` class:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Capivv.configure(
            context = this,
            config = CapivvConfig(
                apiKey = "capivv_secret_your_api_key",
                debug = BuildConfig.DEBUG
            )
        )
    }
}
```

### 2. Identify Users

```kotlin
// Identify a user (call after login)
lifecycleScope.launch {
    Capivv.identify(
        userId = "user_123",
        email = "user@example.com",
        attributes = mapOf("plan" to "free")
    ).onSuccess { result ->
        // User identified, entitlements loaded
    }
}
```

### 3. Check Entitlements

```kotlin
// Check if user has a specific entitlement
if (Capivv.hasEntitlement("premium")) {
    // Show premium features
}

// Observe entitlements reactively
lifecycleScope.launch {
    Capivv.entitlements.collect { entitlements ->
        updateUI(entitlements)
    }
}
```

### 4. Show Paywall

Using the built-in PaywallActivity:

```kotlin
PaywallActivity.launch(
    context = this,
    offeringId = "default"
)
```

Or embed the composable directly:

```kotlin
@Composable
fun MyScreen() {
    var showPaywall by remember { mutableStateOf(false) }

    if (showPaywall) {
        PaywallScreen(
            offering = offering,
            config = PaywallConfig(
                title = "Go Premium",
                subtitle = "Unlock all features",
                features = listOf(
                    "Unlimited access",
                    "No ads",
                    "Priority support"
                )
            ),
            selectedProduct = selectedProduct,
            onProductSelected = { product -> selectedProduct = product },
            onPurchase = { product ->
                // Handle purchase
            },
            onRestore = { /* Handle restore */ },
            onDismiss = { showPaywall = false }
        )
    }
}
```

### 5. Make a Purchase

```kotlin
lifecycleScope.launch {
    Capivv.purchase(activity, product).fold(
        onSuccess = { result ->
            if (result.success) {
                // Purchase successful!
                Toast.makeText(this, "Welcome to Premium!", Toast.LENGTH_SHORT).show()
            }
        },
        onFailure = { error ->
            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        }
    )
}
```

### 6. Restore Purchases

```kotlin
lifecycleScope.launch {
    Capivv.restorePurchases().fold(
        onSuccess = { result ->
            if (result.entitlements.isNotEmpty()) {
                Toast.makeText(this, "Purchases restored!", Toast.LENGTH_SHORT).show()
            }
        },
        onFailure = { error ->
            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        }
    )
}
```

## Dynamic Templates

Load templates from the server for OTA updates:

```kotlin
lifecycleScope.launch {
    val (offering, template) = Capivv.getPaywallWithTemplate(
        offeringId = "default",
        templateIdentifier = "premium_paywall"
    )

    if (template != null) {
        // Use dynamic template
        DynamicPaywallScreen(
            template = template,
            offering = offering,
            // ...
        )
    } else {
        // Fall back to default paywall
        PaywallScreen(
            offering = offering,
            // ...
        )
    }
}
```

## Localization

The SDK supports 8 languages:
- English (en)
- Spanish (es)
- French (fr)
- German (de)
- Japanese (ja)
- Chinese (zh)
- Portuguese (pt)
- Italian (it)

The SDK automatically uses the device's locale. You can also explicitly set the locale:

```kotlin
val localizedText = CapivvL10n.get("subscribe_now", "es") // "Suscribirse Ahora"
```

## Configuration Options

```kotlin
CapivvConfig(
    apiKey = "capivv_secret_...",      // Required: Your API key
    baseUrl = "https://api.capivv.com", // Optional: API base URL
    debug = false,                       // Optional: Enable debug logging
    cacheEnabled = true,                 // Optional: Enable response caching
    cacheTtlSeconds = 300L               // Optional: Cache TTL in seconds
)
```

## Paywall Customization

```kotlin
PaywallConfig(
    title = "Unlock Premium",           // Header title
    subtitle = "Get access to all features", // Optional subtitle
    features = listOf(                  // Feature list
        "Unlimited access",
        "No ads",
        "Priority support"
    ),
    ctaText = "Subscribe Now",          // Purchase button text
    restoreText = "Restore Purchases",  // Restore button text
    showCloseButton = true,             // Show/hide close button
    showRestoreButton = true,           // Show/hide restore button
    termsUrl = "https://...",           // Terms of Service URL
    privacyUrl = "https://...",         // Privacy Policy URL
    themeConfig = CapivvThemeConfig(    // Theme customization
        primaryColor = Color(0xFF6366F1),
        secondaryColor = Color(0xFF10B981),
        useDarkTheme = null // null = follow system
    )
)
```

## Requirements

- Android API 24+ (Android 7.0)
- Kotlin 1.9+
- Jetpack Compose

## ProGuard

If you're using R8/ProGuard, add these rules:

```proguard
-keep class com.capivv.sdk.** { *; }
```

## Support

- Documentation: https://docs.capivv.com
- Issues: https://github.com/capivv/capivv-android-sdk/issues
- Email: support@capivv.com

## License

MIT License - see [LICENSE](LICENSE) for details.
