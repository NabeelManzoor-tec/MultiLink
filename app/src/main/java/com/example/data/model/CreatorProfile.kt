package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creator_profile")
data class CreatorProfile(
    @PrimaryKey val id: Int = 1,
    val handle: String = "alexa_aura",
    val displayName: String = "Alexa Rivers",
    val headline: String = "Digital Artist & Creative Technologist",
    val bio: String = "Building cyberpunk shaders, Indie 3D tools & mentoring creative founders. Transforming pixels into experiences.",
    val avatarDrawableName: String = "creator_avatar_default_1793630325308",
    val location: String = "Tokyo & San Francisco",
    val isVerified: Boolean = true,
    val themeId: String = "CYBER_NEON",
    val customDomain: String = "multilink.bio/@alexa_aura",
    val tipGoalTitle: String = "New Unreal 5 Rendering Rig",
    val tipGoalCurrent: Double = 1420.0,
    val tipGoalTarget: Double = 2500.0,
    // Creator Payout & Tip Collection Methods
    val stripeConnected: Boolean = true,
    val stripeAccountId: String = "acct_1Nx982Multilink",
    val paypalHandle: String = "alexa.rivers.art",
    val cashAppTag: String = "\$AlexaAura",
    val buyMeACoffeeUrl: String = "buymeacoffee.com/alexaaura",
    val preferredTipMethod: String = "Stripe", // "Stripe", "PayPal", "CashApp"
    // MultiLink PRO Tier (Paid Version)
    val isProUser: Boolean = false,
    val proPlanTier: String = "FREE", // "FREE", "PRO_MONTHLY", "PRO_ANNUAL"
    val proPaymentMethod: String = "",
    val proSubscriptionRenewalDate: String = ""
)
