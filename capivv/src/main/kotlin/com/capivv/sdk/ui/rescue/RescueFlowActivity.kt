package com.capivv.sdk.ui.rescue

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.capivv.sdk.Capivv
import kotlinx.coroutines.launch

/**
 * Activity for displaying the cancellation/rescue flow.
 *
 * Launch with:
 * ```kotlin
 * RescueFlowActivity.launch(
 *     context = this,
 *     config = RescueFlowConfig(
 *         offers = listOf(
 *             RescueOffer(
 *                 id = "discount_50",
 *                 type = RescueOfferType.DISCOUNT,
 *                 title = "50% Off for 3 Months",
 *                 description = "Stay with us and save!",
 *                 discountPercent = 50
 *             )
 *         )
 *     )
 * )
 * ```
 */
class RescueFlowActivity : ComponentActivity() {

    companion object {
        const val RESULT_SAVED = Activity.RESULT_FIRST_USER + 1
        const val RESULT_CANCELLED = Activity.RESULT_FIRST_USER + 2
        const val RESULT_DISMISSED = Activity.RESULT_CANCELED

        const val EXTRA_OFFER_ID = "offer_id"
        const val EXTRA_REASON = "reason"
        const val EXTRA_FEEDBACK = "feedback"

        private var pendingConfig: RescueFlowConfig? = null

        /**
         * Launch the rescue flow activity.
         *
         * @param context The activity context
         * @param config Configuration for the rescue flow
         */
        fun launch(
            context: Context,
            config: RescueFlowConfig = RescueFlowConfig()
        ) {
            pendingConfig = config
            val intent = Intent(context, RescueFlowActivity::class.java)
            context.startActivity(intent)
        }

        /**
         * Launch for result to receive flow outcome.
         */
        fun launchForResult(
            activity: Activity,
            requestCode: Int,
            config: RescueFlowConfig = RescueFlowConfig()
        ) {
            pendingConfig = config
            val intent = Intent(activity, RescueFlowActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = pendingConfig ?: RescueFlowConfig()
        pendingConfig = null

        // Track flow started
        trackEvent(RescueFlowEvents.FLOW_STARTED)

        setContent {
            RescueFlowScreen(
                config = config,
                onResult = { result ->
                    handleResult(result)
                }
            )
        }
    }

    private fun handleResult(result: RescueFlowResult) {
        when (result) {
            is RescueFlowResult.Saved -> {
                trackEvent(RescueFlowEvents.OFFER_ACCEPTED, mapOf("offer_id" to result.offerId))

                setResult(RESULT_SAVED, Intent().apply {
                    putExtra(EXTRA_OFFER_ID, result.offerId)
                })
            }

            is RescueFlowResult.Cancelled -> {
                trackEvent(
                    RescueFlowEvents.CANCELLATION_CONFIRMED,
                    mapOf(
                        "reason" to result.reason,
                        "has_feedback" to (result.feedback != null)
                    )
                )

                // Open Play Store subscription management
                openSubscriptionManagement()

                setResult(RESULT_CANCELLED, Intent().apply {
                    putExtra(EXTRA_REASON, result.reason)
                    putExtra(EXTRA_FEEDBACK, result.feedback)
                })
            }

            is RescueFlowResult.Dismissed -> {
                trackEvent(RescueFlowEvents.SUBSCRIPTION_KEPT)
                setResult(RESULT_DISMISSED)
            }
        }

        finish()
    }

    private fun openSubscriptionManagement() {
        Capivv.manageSubscriptions(this)
    }

    private fun trackEvent(event: String, properties: Map<String, Any>? = null) {
        lifecycleScope.launch {
            try {
                Capivv.trackEvent(event, properties)
            } catch (e: Exception) {
                // Ignore tracking errors
            }
        }
    }
}
