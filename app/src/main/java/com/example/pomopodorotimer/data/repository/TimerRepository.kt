package com.example.pomopodorotimer.data.repository

import com.example.pomopodorotimer.model.AppUser
import com.example.pomopodorotimer.model.SessionType
import com.example.pomopodorotimer.model.TimerSession
import com.example.pomopodorotimer.model.UserStats

interface TimerRepository {

    suspend fun createUserDocument(uid: String, email: String)

    suspend fun getUserProfile(uid: String): AppUser?

    suspend fun updateDefaultSession(uid: String, sessionType: SessionType)

    suspend fun saveCompletedSession(uid: String, session: TimerSession)

    suspend fun getUserStats(uid: String): UserStats

}
