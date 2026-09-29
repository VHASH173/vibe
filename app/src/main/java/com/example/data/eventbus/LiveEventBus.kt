package com.example.data.eventbus

import com.example.model.ChatMessage
import com.example.model.Gift
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class LiveStreamEvent {
    data class GiftReceived(
        val gift: Gift,
        val senderName: String,
        val creatorEarningsUsd: Double,
        val creatorCoins: Int
    ) : LiveStreamEvent()

    data class NewChatMessage(
        val message: ChatMessage
    ) : LiveStreamEvent()

    data class HeartReaction(
        val colorHex: Long,
        val xPercent: Float
    ) : LiveStreamEvent()

    data class ViewerCountChanged(
        val count: Int
    ) : LiveStreamEvent()

    data class FilterChanged(
        val filterName: String
    ) : LiveStreamEvent()
}

/**
 * Real-time event bus mimicking Supabase Realtime Channels & WebSockets
 */
object LiveEventBus {
    private val _events = MutableSharedFlow<LiveStreamEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<LiveStreamEvent> = _events.asSharedFlow()

    suspend fun emitGift(gift: Gift, senderName: String) {
        val creatorEarnings = gift.creatorShareUsd
        val creatorCoins = gift.creatorShareCoins
        _events.emit(
            LiveStreamEvent.GiftReceived(
                gift = gift,
                senderName = senderName,
                creatorEarningsUsd = creatorEarnings,
                creatorCoins = creatorCoins
            )
        )
    }

    suspend fun emitChat(message: ChatMessage) {
        _events.emit(LiveStreamEvent.NewChatMessage(message))
    }

    suspend fun emitHeart(colorHex: Long, xPercent: Float) {
        _events.emit(LiveStreamEvent.HeartReaction(colorHex, xPercent))
    }

    suspend fun emitViewerCount(count: Int) {
        _events.emit(LiveStreamEvent.ViewerCountChanged(count))
    }

    suspend fun emitFilterChanged(filterName: String) {
        _events.emit(LiveStreamEvent.FilterChanged(filterName))
    }
}
