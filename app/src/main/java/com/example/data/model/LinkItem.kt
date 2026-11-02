package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LinkCategory {
    SOCIAL,
    PRODUCT,
    SERVICE,
    TIP,
    AFFILIATE
}

@Entity(tableName = "links")
data class LinkItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val category: LinkCategory,
    val iconKey: String, // e.g. "youtube", "instagram", "tiktok", "twitter", "github", "discord", "spotify", "book", "code", "calendar", "coffee", "sparkles"
    val subtitle: String = "",
    val price: String = "",
    val badge: String = "", // e.g. "HOT", "BESTSELLER", "20% OFF", "POPULAR"
    val coverImageName: String = "", // e.g. "digital_product_mockup_1793630338547"
    val isFeatured: Boolean = false,
    val isActive: Boolean = true,
    val orderIndex: Int = 0,
    val totalClicks: Int = 0
)
