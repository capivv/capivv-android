package com.capivv.sdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User information for the Capivv backend.
 *
 * @param userId The unique user identifier
 * @param email Optional email address
 * @param attributes Additional custom attributes
 */
@Serializable
data class UserInfo(
    @SerialName("user_id")
    val userId: String,
    val email: String? = null,
    val attributes: Map<String, String>? = null
)

/**
 * Response from identifying a user.
 */
@Serializable
data class IdentifyResult(
    @SerialName("user_id")
    val userId: String,
    val created: Boolean = false,
    val entitlements: List<Entitlement> = emptyList()
)
