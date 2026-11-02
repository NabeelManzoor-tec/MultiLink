package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "click_events")
data class ClickEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val linkId: Long,
    val linkTitle: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sourcePlatform: String = "Direct / QR Scan",
    val country: String = "United States",
    val device: String = "Mobile",
    val revenueAmount: Double = 0.0
)
