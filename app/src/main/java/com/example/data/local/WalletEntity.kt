package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey
    val id: String = "primary_user_wallet",
    val coinsBalance: Int = 1250, // Starting balance for demo
    val creatorUsdBalance: Double = 142.50, // Creator earnings
    val lifetimeReceivedUsd: Double = 350.00,
    val lifetimeSentCoins: Int = 2400
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // GIFT_SENT, GIFT_RECEIVED, COIN_PURCHASE, WITHDRAWAL
    val description: String,
    val coinsAmount: Int,
    val usdAmount: Double,
    val creatorShareUsd: Double,
    val platformShareUsd: Double,
    val counterpartName: String,
    val timestamp: Long = System.currentTimeMillis()
)
