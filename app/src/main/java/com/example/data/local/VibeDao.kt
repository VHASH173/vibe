package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VibeDao {
    // Wallet
    @Query("SELECT * FROM wallet WHERE id = 'primary_user_wallet'")
    fun getWalletFlow(): Flow<WalletEntity?>

    @Query("SELECT * FROM wallet WHERE id = 'primary_user_wallet'")
    suspend fun getWalletOnce(): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity)

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getTransactionsFlow(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    // Video Posts
    @Query("SELECT * FROM video_posts")
    fun getVideoPostsFlow(): Flow<List<VideoPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideoPosts(posts: List<VideoPostEntity>)

    @Update
    suspend fun updateVideoPost(post: VideoPostEntity)

    // Live Streams
    @Query("SELECT * FROM live_streams WHERE isLiveNow = 1")
    fun getActiveLiveStreamsFlow(): Flow<List<LiveStreamEntity>>

    @Query("SELECT * FROM live_streams WHERE id = :id")
    suspend fun getLiveStreamById(id: String): LiveStreamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveStreams(streams: List<LiveStreamEntity>)

    @Update
    suspend fun updateLiveStream(stream: LiveStreamEntity)

    // GDPR Right to be Forgotten
    @Query("DELETE FROM wallet")
    suspend fun clearWallet()

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM video_posts")
    suspend fun clearVideoPosts()

    @Query("DELETE FROM live_streams")
    suspend fun clearLiveStreams()
}

