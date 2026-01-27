package com.capivv.example

import android.app.Application
import com.capivv.sdk.Capivv
import com.capivv.sdk.models.CapivvConfig

class CapivvExampleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Capivv SDK
        Capivv.configure(
            context = this,
            config = CapivvConfig(
                apiKey = "capivv_pk_test_YOUR_API_KEY",
                debug = true
            )
        )
    }
}
