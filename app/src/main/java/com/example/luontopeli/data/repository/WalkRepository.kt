package com.example.luontopeli.data.repository

import com.example.luontopeli.data.local.dao.WalkSessionDao
import com.example.luontopeli.data.local.entity.WalkSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository-luokka kävelykertojen hallintaan.
 */
@Singleton
class WalkRepository @Inject constructor(
    private val walkSessionDao: WalkSessionDao
) {
    /** Flow-virta kaikista kävelykerroista aikajärjestyksessä (uusin ensin) */
    val allSessions: Flow<List<WalkSession>> = walkSessionDao.getAllSessions()

    /**
     * Tallentaa uuden kävelykerran tietokantaan.
     */
    suspend fun insertSession(session: WalkSession) {
        walkSessionDao.insert(session)
    }

    /**
     * Päivittää olemassa olevan kävelykerran tiedot.
     */
    suspend fun updateSession(session: WalkSession) {
        walkSessionDao.update(session)
    }

    /**
     * Hakee parhaillaan aktiivisen kävelykerran.
     */
    suspend fun getActiveSession(): WalkSession? {
        return walkSessionDao.getActiveSession()
    }

    suspend fun deleteSession(session: WalkSession) {
        walkSessionDao.delete(session)
    }
}
