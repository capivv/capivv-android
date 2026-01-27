# Capivv Android Example

This example app demonstrates how to integrate the Capivv SDK into an Android application using Jetpack Compose.

## Features Demonstrated

- SDK initialization in Application class
- User identification
- Entitlement checking
- Paywall presentation

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android device or emulator running API 24+

### Setup

1. Open the `example` folder in Android Studio
2. Replace `capivv_pk_test_YOUR_API_KEY` in `CapivvExampleApp.kt` with your actual API key
3. Sync Gradle and run the app

### Configuration

The SDK is initialized in `CapivvExampleApp.kt`:

```kotlin
Capivv.configure(
    context = this,
    config = CapivvConfig(
        apiKey = "capivv_pk_YOUR_API_KEY",
        debug = true  // Set to false in production
    )
)
```

### User Identification

Identify users when they log in:

```kotlin
Capivv.identify(
    userId = "user-123",
    attributes = mapOf(
        "email" to "user@example.com",
        "name" to "John Doe"
    )
)
```

### Check Entitlements

Check if a user has access to premium features:

```kotlin
val hasPremium = Capivv.hasEntitlement("premium")
if (hasPremium) {
    // Show premium content
} else {
    // Show paywall
}
```

### Show Paywall

Present the paywall to users:

```kotlin
PaywallActivity.launch(context)
```

Or use the Composable directly:

```kotlin
PaywallScreen(
    onPurchaseComplete = { result ->
        // Handle purchase completion
    },
    onDismiss = {
        // Handle dismissal
    }
)
```

## Project Structure

```
example/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/com/capivv/example/
│       │   ├── CapivvExampleApp.kt    # SDK initialization
│       │   └── MainActivity.kt         # Main example screen
│       └── res/
│           └── values/
│               ├── strings.xml
│               └── themes.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml
```

## Testing with Sandbox

For testing purchases, use Google Play's test track:

1. Upload your app to the internal test track
2. Add testers to the track
3. Use license testing accounts

## Troubleshooting

### BillingClient connection issues

Ensure you have:
- `com.android.vending.BILLING` permission in AndroidManifest.xml
- A signed APK (debug signing works for testing)
- Google Play Store installed on the device

### Entitlements not updating

Call `Capivv.syncPurchases()` to force a refresh from the server.
