package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_posts")
data class VideoPostEntity(
    @PrimaryKey
    val id: String,
    val creatorHandle: String,
    val creatorName: String,
    val creatorAvatar: String,
    val caption: String,
    val musicTitle: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val commentsCount: Int,
    val sharesCount: Int,
    val giftsCount: Int,
    val tags: String,
    val bgTheme: String = "cyber" // cyber, synthwave, anime, neon, dragon
)

@Entity(tableName = "live_streams")
data class LiveStreamEntity(
    @PrimaryKey
    val id: String,
    val streamerHandle: String,
    val streamerName: String,
    val streamerAvatar: String,
    val title: String,
    val category: String,
    val viewerCount: Int,
    val likesCount: Int,
    val activeFilterName: String = "CYBER_NEON",
    val isLiveNow: Boolean = true,
    val streamBitrateKbps: Int = 4500,
    val streamLatencyMs: Int = 28
)
