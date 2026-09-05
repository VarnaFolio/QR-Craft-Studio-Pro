package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QRCodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val content: String,
    val type: String, // "Generated" or "Scanned"
    val category: String, // "URL Link", "Wi-Fi", "Free Text", "Email", etc.
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
