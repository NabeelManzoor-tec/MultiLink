package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creator_profile")
data class CreatorProfile(
    @PrimaryKey val id: Int = 1,
    val handle: String = "nabeeljutt",
    val displayName: String = "Nabeel Jutt",
    val headline: String = "Digital Creator & Builder",
    val bio: String = "Welcome to my official MultiLink hub! Explore my projects, tools, socials, and direct support.",
    val avatarDrawableName: String = "creator_avatar_default_1793630325308",
    val location: String = "Global",
    val isVerified: Boolean = true,
    val themeId: String = "CYBER_NEON",
    val customDomain: String = "gen-lang-client-0468598937.web.app/?handle=nabeeljutt",
    val tipGoalTitle: String = "Creator Studio & Production Setup",
    val tipGoalCurrent: Double = 0.0,
    val tipGoalTarget: Double = 1000.0,
    // Creator Payout & Tip Collection Methods
    val stripeConnected: Boolean = false,
    val stripeAccountId: String = "",
    val paypalHandle: String = "nabeeljuttm424",
    val cashAppTag: String = "",
    val buyMeACoffeeUrl: String = "",
    val preferredTipMethod: String = "PayPal", // "PayPal", "Stripe", "CashApp"
    // MultiLink PRO Tier (Paid Version)
    val isProUser: Boolean = false,
    val proPlanTier: String = "FREE", // "FREE", "PRO_MONTHLY", "PRO_ANNUAL"
    val proPaymentMethod: String = "",
    val proSubscriptionRenewalDate: String = "",
    val proPurchaseToken: String = "",
    val proOrderId: String = ""
)
