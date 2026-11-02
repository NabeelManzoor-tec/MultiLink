package com.example.data.model

import com.google.firebase.Timestamp

enum class UserRole {
    CREATOR,
    CUSTOMER
}

data class UserAccount(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val role: UserRole = UserRole.CREATOR,
    val createdAt: Timestamp? = null
)

data class CustomerOrder(
    val id: String = "",
    val itemTitle: String = "",
    val itemCategory: String = "", // "PRODUCT", "SERVICE", "TIP"
    val price: Double = 0.0,
    val creatorHandle: String = "",
    val creatorName: String = "",
    val status: String = "COMPLETED", // "COMPLETED", "CONFIRMED", "ACTIVE"
    val downloadUrl: String = "",
    val sessionDate: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
