package com.capivv.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.capivv.sdk.Capivv
import com.capivv.sdk.ui.paywall.PaywallActivity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExampleScreen(
                        onIdentify = { userId ->
                            lifecycleScope.launch {
                                Capivv.identify(
                                    userId = userId,
                                    attributes = mapOf("source" to "example_app")
                                )
                            }
                        },
                        onShowPaywall = {
                            PaywallActivity.launch(this@MainActivity)
                        },
                        onCheckEntitlements = {
                            lifecycleScope.launch {
                                val hasAccess = Capivv.hasEntitlement("premium")
                                // Handle result
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExampleScreen(
    onIdentify: (String) -> Unit,
    onShowPaywall: () -> Unit,
    onCheckEntitlements: () -> Unit
) {
    var userId by remember { mutableStateOf("test-user-123") }
    var isIdentified by remember { mutableStateOf(false) }
    var hasPremium by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Capivv SDK Example",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // User Identification Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "1. Identify User",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isIdentified
                )

                Button(
                    onClick = {
                        isLoading = true
                        onIdentify(userId)
                        isIdentified = true
                        isLoading = false
                    },
                    enabled = userId.isNotBlank() && !isIdentified && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isIdentified) "Identified as $userId" else "Identify")
                }
            }
        }

        // Entitlements Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "2. Check Entitlements",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Premium Access:")
                    Text(
                        text = if (hasPremium) "Active" else "Not Active",
                        color = if (hasPremium) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        isLoading = true
                        onCheckEntitlements()
                        // In a real app, update hasPremium based on result
                        isLoading = false
                    },
                    enabled = isIdentified && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Refresh Entitlements")
                }
            }
        }

        // Paywall Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "3. Show Paywall",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Display the paywall to allow users to subscribe.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onShowPaywall,
                    enabled = isIdentified && !hasPremium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show Paywall")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Premium Content Preview
        if (hasPremium) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Premium Content Unlocked!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "You have access to all premium features.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
