package com.example.model

data class CoinPackage(
    val id: String,
    val coins: Int,
    val bonusCoins: Int = 0,
    val usdPrice: Double,
    val badge: String? = null
) {
    val totalCoins: Int get() = coins + bonusCoins
    val formattedPrice: String get() = String.format("$%.2f", usdPrice)
}

object CoinStoreCatalog {
    val packages = listOf(
        CoinPackage("pack_100", 100, 0, 0.99, "Básico"),
        CoinPackage("pack_500", 500, 25, 4.49, "+5% Extra"),
        CoinPackage("pack_1000", 1000, 100, 8.99, "🔥 Popular"),
        CoinPackage("pack_2500", 2500, 350, 21.99, "+14% Extra"),
        CoinPackage("pack_5000", 5000, 1000, 42.99, "👑 Mejor Valor"),
        CoinPackage("pack_10000", 10000, 2500, 79.99, "VIP Streamer")
    )
}
