package com.example.service

import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.model.GiftModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Repositorio con consultas a Firestore (colección 'gifts') y precarga de iconos en caché de disco con Coil.
 * Project ID: tiktok-live-app-c6854
 * Bucket: tiktok-live-app-c6854.firebasestorage.app
 * Colección Firestore: gifts
 */
class FirebaseGiftRepository(
    private val context: Context,
    customFirestore: FirebaseFirestore? = null
) {
    companion object {
        private const val TAG = "FirebaseGiftRepository"
        private const val DEBUG_TAG = "FirestoreGiftDebug"
        private const val COLLECTION_GIFTS = "gifts"
        private const val QUERY_LIMIT = 100L

        // Cache local en memoria para optimizar lecturas sucesivas
        private val giftCache = ConcurrentHashMap<Long, GiftModel>()
    }

    private val firestoreInstance: FirebaseFirestore? = customFirestore ?: try {
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            val options = com.google.firebase.FirebaseOptions.Builder()
                .setProjectId("tiktok-live-app-c6854")
                .setApplicationId("com.example")
                .setApiKey("AIzaSyMockKeyForInitializationOnly")
                .setStorageBucket("tiktok-live-app-c6854.firebasestorage.app")
                .build()
            com.google.firebase.FirebaseApp.initializeApp(context, options)
        }
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.w(TAG, "FirebaseApp no inicializado o sin conexión: ${e.message}")
        null
    }

    /**
     * Utilidad de depuración para interceptar y validar paquetes de datos de Firestore antes de la deserialización.
     */
    private fun debugLogFirestoreDocument(doc: com.google.firebase.firestore.DocumentSnapshot) {
        try {
            val data = doc.data
            Log.d(DEBUG_TAG, "--- [PAQUETE FIRESTORE DOC: ${doc.id}] ---")
            Log.d(DEBUG_TAG, "Contenido crudo: $data")

            if (data == null) {
                Log.w(DEBUG_TAG, "Documento ${doc.id} está completamente vacío (null data).")
                return
            }

            // Validar tipos y nulabilidad
            val rawId = data["id"]
            if (rawId == null) {
                Log.w(DEBUG_TAG, "Doc ${doc.id}: Campo 'id' es nulo. Se usará doc.id (${doc.id}).")
            } else if (rawId !is Number && rawId !is String) {
                Log.w(DEBUG_TAG, "Doc ${doc.id}: Mismatch de esquema en 'id'. Tipo real: ${rawId.javaClass.simpleName} (Valor: $rawId)")
            }

            val rawDiamond = data["diamond"]
            if (rawDiamond == null) {
                Log.w(DEBUG_TAG, "Doc ${doc.id}: Campo 'diamond' es nulo. Asignando 0.")
            } else if (rawDiamond !is Number) {
                Log.w(DEBUG_TAG, "Doc ${doc.id}: Mismatch de esquema en 'diamond'. Tipo: ${rawDiamond.javaClass.simpleName}")
            }

            val rawStorageIcon = data["storageIcon"] as? String
            if (rawStorageIcon.isNullOrEmpty()) {
                Log.i(DEBUG_TAG, "Doc ${doc.id}: 'storageIcon' vacío, se verificará 'picture'.")
            }
        } catch (e: Exception) {
            Log.w(DEBUG_TAG, "Error durante inspección de depuración para ${doc.id}: ${e.message}")
        }
    }

    /**
     * Obtiene el catálogo de regalos desde Firestore con reintentos y backoff exponencial en caso de fallo de red.
     */
    suspend fun getAllGiftsWithRetry(maxAttempts: Int = 3): List<GiftModel> = withContext(Dispatchers.IO) {
        var currentDelay = 500L
        for (attempt in 1..maxAttempts) {
            try {
                Log.d(TAG, "Intento $attempt de $maxAttempts para cargar catálogo de Firestore...")
                val gifts = getAllGifts()
                if (gifts.isNotEmpty()) {
                    return@withContext gifts
                }
            } catch (e: Exception) {
                Log.w(TAG, "Fallo de red en intento $attempt: ${e.message}")
            }
            if (attempt < maxAttempts) {
                Log.d(TAG, "Esperando ${currentDelay}ms antes del siguiente reintento...")
                kotlinx.coroutines.delay(currentDelay)
                currentDelay *= 2
            }
        }
        Log.w(TAG, "Se alcanzaron los $maxAttempts reintentos sin éxito. Usando catálogo local de respaldo.")
        return@withContext getDefaultFallbackGifts()
    }

    /**
     * Obtiene el catálogo de regalos desde Firestore con límite de seguridad de 100 elementos
     * ordenado por diamond ascendente.
     */
    suspend fun getAllGifts(): List<GiftModel> = withContext(Dispatchers.IO) {
        val firestore = firestoreInstance ?: return@withContext getDefaultFallbackGifts()
        return@withContext try {
            val querySnapshot = firestore.collection(COLLECTION_GIFTS)
                .orderBy("diamond", Query.Direction.ASCENDING)
                .limit(QUERY_LIMIT)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val list = querySnapshot.documents.mapNotNull { doc ->
                    debugLogFirestoreDocument(doc)
                    try {
                        val rawId = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L
                        val rawDiamond = doc.getLong("diamond") ?: 0L
                        val rawType = doc.getLong("type") ?: 0L
                        val rawName = doc.getString("name") ?: ""
                        val rawStorage = doc.getString("storageIcon") ?: ""
                        val rawPic = doc.getString("picture") ?: ""

                        val model = try {
                            doc.toObject(GiftModel::class.java)
                        } catch (de: Exception) {
                            Log.w(DEBUG_TAG, "toObject falló para ${doc.id}, usando mapeo manual: ${de.message}")
                            null
                        } ?: GiftModel(
                            id = rawId,
                            name = rawName,
                            diamond = rawDiamond,
                            type = rawType,
                            storageIcon = rawStorage,
                            picture = rawPic
                        )
                        giftCache[model.id] = model
                        model
                    } catch (e: Exception) {
                        Log.w(TAG, "Documento ignorado por formato inválido: ${doc.id}", e)
                        null
                    }
                }
                Log.d(TAG, "Catálogo de ${list.size} regalos cargado exitosamente de Firestore.")
                if (list.isNotEmpty()) list else getDefaultFallbackGifts()
            } else {
                Log.w(TAG, "Colección 'gifts' vacía en Firestore, usando catálogo inicial.")
                getDefaultFallbackGifts()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Fallback de Firestore al leer catálogo: ${e.message}")
            getDefaultFallbackGifts()
        }
    }

    /**
     * Flow reactivo para observar los regalos disponibles con límite seguro de 100 elementos
     */
    fun getAvailableGiftsFlow(): Flow<List<GiftModel>> = callbackFlow {
        val firestore = firestoreInstance
        if (firestore == null) {
            trySend(getDefaultFallbackGifts())
            channel.close()
            return@callbackFlow
        }

        val listenerRegistration = try {
            firestore.collection(COLLECTION_GIFTS)
                .orderBy("diamond", Query.Direction.ASCENDING)
                .limit(QUERY_LIMIT)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Aviso de snapshot en Firestore gifts (posible offline o permiso): ${error.message}")
                        trySend(getDefaultFallbackGifts())
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val gifts = snapshot.documents.mapNotNull { doc ->
                            try {
                                val rawId = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L
                                val rawDiamond = doc.getLong("diamond") ?: 0L
                                val rawType = doc.getLong("type") ?: 0L
                                val rawName = doc.getString("name") ?: ""
                                val rawStorage = doc.getString("storageIcon") ?: ""
                                val rawPic = doc.getString("picture") ?: ""

                                val model = try {
                                    doc.toObject(GiftModel::class.java)
                                } catch (de: Exception) {
                                    null
                                } ?: GiftModel(
                                    id = rawId,
                                    name = rawName,
                                    diamond = rawDiamond,
                                    type = rawType,
                                    storageIcon = rawStorage,
                                    picture = rawPic
                                )
                                giftCache[model.id] = model
                                model
                            } catch (e: Exception) {
                                Log.w(TAG, "Error deserializando doc ${doc.id}: ${e.message}")
                                null
                            }
                        }
                        if (gifts.isNotEmpty()) {
                            trySend(gifts)
                        } else {
                            trySend(getDefaultFallbackGifts())
                        }
                    } else {
                        trySend(getDefaultFallbackGifts())
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo adjuntar snapshot listener, enviando catálogo local: ${e.message}")
            trySend(getDefaultFallbackGifts())
            null
        }

        awaitClose {
            try {
                listenerRegistration?.remove()
            } catch (e: Exception) {
                Log.w(TAG, "Error cerrando listener de Firestore: ${e.message}")
            }
        }
    }

    /**
     * Catálogo local de respaldo si Firestore no está disponible en modo offline
     */
    private fun getDefaultFallbackGifts(): List<GiftModel> {
        val baseBucket = "https://firebasestorage.googleapis.com/v0/b/tiktok-live-app-c6854.firebasestorage.app/o"
        return listOf(
            GiftModel(id = 5269L, name = "Rosa Neón", diamond = 10L, type = 1L, storageIcon = "$baseBucket/rose.webp?alt=media", picture = "https://p16-webcast.tiktokcdn.com/rose.png"),
            GiftModel(id = 5482L, name = "Gafas Cyber", diamond = 50L, type = 1L, storageIcon = "$baseBucket/shades.webp?alt=media", picture = "https://p16-webcast.tiktokcdn.com/shades.png"),
            GiftModel(id = 5590L, name = "Holo Visor", diamond = 100L, type = 1L, storageIcon = "$baseBucket/visor.webp?alt=media", picture = "https://p16-webcast.tiktokcdn.com/visor.png"),
            GiftModel(id = 5655L, name = "Cohete Vibe", diamond = 500L, type = 2L, storageIcon = "$baseBucket/rocket.webp?alt=media", picture = "https://p16-webcast.tiktokcdn.com/rocket.png"),
            GiftModel(id = 5827L, name = "Dragón Galaxia", diamond = 1000L, type = 3L, storageIcon = "$baseBucket/dragon.webp?alt=media", picture = "https://p16-webcast.tiktokcdn.com/dragon.png"),
            GiftModel(id = 6001L, name = "Corona Dorada", diamond = 2500L, type = 3L, storageIcon = "$baseBucket/crown.webp?alt=media", picture = "https://p16-webcast.tiktokcdn.com/crown.png"),
            GiftModel(id = 6120L, name = "Trofeo Diamante", diamond = 5000L, type = 3L, storageIcon = "$baseBucket/trophy.webp?alt=media", picture = "https://p16-webcast.tiktokcdn.com/trophy.png"),
            GiftModel(id = 6250L, name = "Cyber Cat", diamond = 800L, type = 2L, storageIcon = "$baseBucket/cat.webp?alt=media", picture = "https://p16-webcast.tiktokcdn.com/cat.png"),
            GiftModel(id = 6380L, name = "Aura de Fuego", diamond = 1500L, type = 2L, storageIcon = "$baseBucket/fire.webp?alt=media", picture = "https://p16-webcast.tiktokcdn.com/fire.png")
        ).also { list ->
            list.forEach { giftCache[it.id] = it }
        }
    }

    /**
     * Consulta Firestore colección gifts, doc giftId.toString()
     */
    suspend fun getGiftById(giftId: Long): GiftModel? = withContext(Dispatchers.IO) {
        giftCache[giftId]?.let { return@withContext it }

        val firestore = firestoreInstance ?: return@withContext getDefaultFallbackGifts().find { it.id == giftId }
        return@withContext try {
            val docSnapshot = firestore.collection(COLLECTION_GIFTS)
                .document(giftId.toString())
                .get()
                .await()

            if (docSnapshot.exists()) {
                val rawId = docSnapshot.getLong("id") ?: docSnapshot.id.toLongOrNull() ?: giftId
                val rawDiamond = docSnapshot.getLong("diamond") ?: 0L
                val rawType = docSnapshot.getLong("type") ?: 0L
                val rawName = docSnapshot.getString("name") ?: ""
                val rawStorage = docSnapshot.getString("storageIcon") ?: ""
                val rawPic = docSnapshot.getString("picture") ?: ""

                val model = try {
                    docSnapshot.toObject(GiftModel::class.java)
                } catch (de: Exception) {
                    null
                } ?: GiftModel(
                    id = rawId,
                    name = rawName,
                    diamond = rawDiamond,
                    type = rawType,
                    storageIcon = rawStorage,
                    picture = rawPic
                )
                giftCache[giftId] = model
                Log.d(TAG, "Documento de regalo recuperado de Firestore: $model")
                model
            } else {
                Log.w(TAG, "No se encontró el documento de regalo para ID: $giftId")
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error consultando Firestore para giftId $giftId: ${e.message}")
            null
        }
    }

    suspend fun getGiftById(giftId: Int): GiftModel? = getGiftById(giftId.toLong())

    /**
     * Precarga los .webp de storageIcon con Coil en la caché de disco
     */
    fun preloadGiftIcons(ids: List<Long>) {
        val imageLoader = ImageLoader(context)
        for (id in ids) {
            val gift = giftCache[id]
            val iconUrl = gift?.storageIcon?.ifEmpty { gift.picture }
            if (!iconUrl.isNullOrEmpty()) {
                val request = ImageRequest.Builder(context)
                    .data(iconUrl)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
                imageLoader.enqueue(request)
                Log.d(TAG, "Icono .webp precargado en caché de disco para regalo $id: $iconUrl")
            }
        }
    }
}
