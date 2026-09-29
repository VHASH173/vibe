package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WalletEntity::class,
        TransactionEntity::class,
        VideoPostEntity::class,
        LiveStreamEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VibeDatabase : RoomDatabase() {
    abstract fun vibeDao(): VibeDao

    companion object {
        @Volatile
        private var INSTANCE: VibeDatabase? = null

        fun getDatabase(context: Context): VibeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VibeDatabase::class.java,
                    "vibestream_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial data asynchronously
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getDatabase(context).vibeDao()
                            seedInitialData(dao)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: VibeDao) {
            dao.insertWallet(
                WalletEntity(
                    id = "primary_user_wallet",
                    coinsBalance = 1500,
                    creatorUsdBalance = 245.50,
                    lifetimeReceivedUsd = 480.00,
                    lifetimeSentCoins = 3200
                )
            )

            dao.insertTransaction(
                TransactionEntity(
                    type = "GIFT_RECEIVED",
                    description = "Regalo Cohete Vibe de @alex_vibe",
                    coinsAmount = 1000,
                    usdAmount = 10.00,
                    creatorShareUsd = 7.50,
                    platformShareUsd = 2.50,
                    counterpartName = "@alex_vibe",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    type = "GIFT_RECEIVED",
                    description = "Regalo Corona de Oro de @sofia_tech",
                    coinsAmount = 500,
                    usdAmount = 5.00,
                    creatorShareUsd = 3.75,
                    platformShareUsd = 1.25,
                    counterpartName = "@sofia_tech",
                    timestamp = System.currentTimeMillis() - 7200000
                )
            )

            dao.insertVideoPosts(
                listOf(
                    VideoPostEntity(
                        id = "video_1",
                        creatorHandle = "@cyber_kiri",
                        creatorName = "Kiri VTuber",
                        creatorAvatar = "🐱",
                        caption = "¡Probando el nuevo filtro holográfico en vivo! 🚀✨ ¿Qué tal se ve el visor de neón? Apoyen con rosas para desbloquear el dragón!",
                        musicTitle = "Cyber City Wave - VibeStream Sound Studio",
                        likesCount = 28400,
                        isLiked = false,
                        commentsCount = 1420,
                        sharesCount = 590,
                        giftsCount = 380,
                        tags = "#vtuber #cyberpunk #livestream #vibestream",
                        bgTheme = "cyber"
                    ),
                    VideoPostEntity(
                        id = "video_2",
                        creatorHandle = "@dj_marcus",
                        creatorName = "Marcus Beats",
                        creatorAvatar = "🎧",
                        caption = "Creando un track de Synthwave en vivo con la IA de VibeStream 🔥🎶 ¡Envíen regalos para cambiar el ritmo y activar el fuego!",
                        musicTitle = "Neon Midnight 128BPM - DJ Marcus",
                        likesCount = 45100,
                        isLiked = true,
                        commentsCount = 2390,
                        sharesCount = 1120,
                        giftsCount = 890,
                        tags = "#music #dj #synthesizer #livebeat",
                        bgTheme = "synthwave"
                    ),
                    VideoPostEntity(
                        id = "video_3",
                        creatorHandle = "@luna_cosplay",
                        creatorName = "Luna Stellar",
                        creatorAvatar = "🦊",
                        caption = "Transmisión especial de Cosplay Mech Warrior con filtro AR de partículas! Gracias por la comisión justa del 75% 💜",
                        musicTitle = "Galactic Odyssey - Luna Sound",
                        likesCount = 51200,
                        isLiked = false,
                        commentsCount = 3100,
                        sharesCount = 1840,
                        giftsCount = 1250,
                        tags = "#cosplay #anime #faircreator #75percent",
                        bgTheme = "anime"
                    ),
                    VideoPostEntity(
                        id = "video_4",
                        creatorHandle = "@tech_alvaro",
                        creatorName = "Álvaro Dev",
                        creatorAvatar = "⚡",
                        caption = "Explicando la arquitectura WebRTC y Supabase Edge Functions detrás de VibeStream y su modelo 75/25 💻⚡",
                        musicTitle = "Lofi Code Chill - AlvaroTech",
                        likesCount = 18900,
                        isLiked = false,
                        commentsCount = 980,
                        sharesCount = 430,
                        giftsCount = 210,
                        tags = "#tech #coding #webrtc #supabase",
                        bgTheme = "neon"
                    )
                )
            )

            dao.insertLiveStreams(
                listOf(
                    LiveStreamEntity(
                        id = "live_1",
                        streamerHandle = "@cyber_kiri",
                        streamerName = "Kiri VTuber",
                        streamerAvatar = "🐱",
                        title = "🔴 VTuber Live: Karaoke Cyberpunk + Filtros AR en tiempo real!",
                        category = "VTuber / Anime",
                        viewerCount = 3420,
                        likesCount = 58900,
                        activeFilterName = "VTUBER_KAWAII"
                    ),
                    LiveStreamEntity(
                        id = "live_2",
                        streamerHandle = "@dj_marcus",
                        streamerName = "Marcus Beats",
                        streamerAvatar = "🎧",
                        title = "🔴 Live DJ Set Synthwave 24/7 | Regalos activan visuales reactivos",
                        category = "Música / DJ",
                        viewerCount = 1890,
                        likesCount = 32100,
                        activeFilterName = "CYBER_NEON"
                    ),
                    LiveStreamEntity(
                        id = "live_3",
                        streamerHandle = "@luna_cosplay",
                        streamerName = "Luna Stellar",
                        streamerAvatar = "🦊",
                        title = "🔴 Just Chatting + Probando filtros de partículas y orejas holo",
                        category = "Charla / IRL",
                        viewerCount = 4210,
                        likesCount = 94500,
                        activeFilterName = "STARDUST_GLOW"
                    ),
                    LiveStreamEntity(
                        id = "live_4",
                        streamerHandle = "@neo_speed",
                        streamerName = "Neo Speedrunner",
                        streamerAvatar = "🏎️",
                        title = "🔴 Torneo Cyber Drift 2026 | Meta de Cohetes para sorteo VIP",
                        category = "Gaming / Esports",
                        viewerCount = 2750,
                        likesCount = 44200,
                        activeFilterName = "FLAME_AURA"
                    )
                )
            )
        }
    }
}
