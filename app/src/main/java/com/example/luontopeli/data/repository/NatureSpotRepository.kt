package com.example.luontopeli.data.repository

import com.example.luontopeli.data.local.dao.NatureSpotDao
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.data.remote.firebase.AuthManager
import com.example.luontopeli.data.remote.firebase.FirestoreManager
import com.example.luontopeli.data.remote.firebase.StorageManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository-luokka luontolöytöjen hallintaan (Repository-suunnittelumalli).
 *
 * Toimii välittäjänä tietolähteiden (Room-tietokanta) ja ViewModelien välillä.
 */
@Singleton
class NatureSpotRepository @Inject constructor(
    private val dao: NatureSpotDao,
    private val firestoreManager: FirestoreManager,
    private val storageManager: StorageManager,
    private val authManager: AuthManager
) {
    /** Flow-virta kaikista luontolöydöistä aikajärjestyksessä (uusin ensin) */
    val allSpots: Flow<List<NatureSpot>> = dao.getAllSpots()

    /** Flow-virta löydöistä joilla on validi GPS-sijainti (kartalla näytettävät) */
    val spotsWithLocation: Flow<List<NatureSpot>> = dao.getSpotsWithLocation()

    /**
     * Tallenna löytö: ensin Room, sitten Firebase.
     */
    suspend fun insertSpot(spot: NatureSpot) {
        val spotWithUser = spot.copy(userId = authManager.currentUserId)

        // 1. Tallenna paikallisesti HETI (toimii offline-tilassakin)
        dao.insert(spotWithUser.copy(synced = false))

        // 2. Yritä synkronoida Firebaseen
        syncSpotToFirebase(spotWithUser)
    }

    /**
     * Synkronoi yksittäinen kohde Firebaseen.
     */
    private suspend fun syncSpotToFirebase(spot: NatureSpot) {
        try {
            // 2a. Lataa kuva Storageen (jos paikallinen kuva olemassa)
            val firebaseImageUrl = spot.imageLocalPath?.let { localPath ->
                storageManager.uploadImage(localPath, spot.id).getOrNull()
            }

            // 2b. Tallenna metadata Firestoreen
            val spotWithUrl = spot.copy(imageFirebaseUrl = firebaseImageUrl, synced = true)
            firestoreManager.saveSpot(spotWithUrl).getOrThrow()

            // 2c. Merkitse Room:ssa synkronoiduksi
            dao.markSynced(spot.id, firebaseImageUrl ?: "")
        } catch (e: Exception) {
            // Synkronointi epäonnistui – yritetään uudelleen myöhemmin
            // synced = false pysyy Room:ssa
        }
    }

    /**
     * Synkronoi kaikki odottavat kohteet (kutsutaan esim. sovelluksen käynnistyessä).
     */
    suspend fun syncPendingSpots() {
        val unsyncedSpots = dao.getUnsyncedSpots()
        unsyncedSpots.forEach { spot ->
            syncSpotToFirebase(spot)
        }
    }

    suspend fun deleteSpot(spot: NatureSpot) {
        dao.delete(spot)
    }
}
