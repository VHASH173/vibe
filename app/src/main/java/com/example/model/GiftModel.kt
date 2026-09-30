package com.example.model

/**
 * Data class que representa un documento de la colección 'gifts' en Firestore.
 * Project ID: tiktok-live-app-c6854
 * Bucket: tiktok-live-app-c6854.firebasestorage.app
 * Colección Firestore: gifts
 */
data class GiftModel(
    val id: Long = 0L,
    val name: String = "",
    val diamond: Long = 0L,
    val type: Long = 0L,
    val storageIcon: String = "", // URL Firebase Storage
    val picture: String = ""      // URL CDN TikTok
) {
    val idInt: Int get() = id.toInt()
    val diamondInt: Int get() = diamond.toInt()
    val typeInt: Int get() = type.toInt()

    constructor(
        id: Int,
        name: String = "",
        diamond: Int = 0,
        type: Int = 0,
        storageIcon: String = "",
        picture: String = ""
    ) : this(id.toLong(), name, diamond.toLong(), type.toLong(), storageIcon, picture)
}
