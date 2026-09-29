package com.example.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderName: String,
    val senderAvatar: String = "",
    val message: String,
    val isDonation: Boolean = false,
    val giftName: String? = null,
    val giftCoins: Int = 0,
    val creatorShareUsd: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
