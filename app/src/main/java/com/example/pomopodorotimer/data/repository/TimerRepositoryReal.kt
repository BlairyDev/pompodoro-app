package com.example.pomopodorotimer.data.repository

import com.example.pomopodorotimer.model.AppUser
import com.example.pomopodorotimer.model.SessionType
import com.example.pomopodorotimer.model.TimerSession
import com.example.pomopodorotimer.model.UserStats
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TimerRepositoryReal @Inject constructor(
    private val firestore: FirebaseFirestore
) : TimerRepository {

    override suspend fun createUserDocument(uid: String, email: String) {
        val userRef = firestore.collection("users").document(uid)
        val snapshot = userRef.get().await()

        if (!snapshot.exists()) {
            val user = AppUser(
                uid = uid,
                email = email,
                createdAt = System.currentTimeMillis(),
                defaultSessionMinutes = 25
            )
            userRef.set(user).await()
        }
    }

    override suspend fun getUserProfile(uid: String): AppUser? {
        return try {
            firestore.collection("users")
                .document(uid)
                .get()
                .await()
                .toObject(AppUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateDefaultSession(uid: String, sessionType: SessionType) {
        firestore.collection("users")
            .document(uid)
            .update("defaultSessionMinutes", sessionType.minutes)
            .await()
    }

    override suspend fun saveCompletedSession(uid: String, session: TimerSession) {
        val sessionRef = firestore.collection("users")
            .document(uid)
            .collection("sessions")
            .document()

        val sessionWithId = session.copy(id = sessionRef.id)
        sessionRef.set(sessionWithId).await()
    }

    override suspend fun getUserStats(uid: String): UserStats {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("sessions")
                .get()
                .await()

            val sessions = snapshot.documents.mapNotNull {
                it.toObject(TimerSession::class.java)
            }

            UserStats(
                totalSessions = sessions.count { it.completed },
                totalFocusMinutes = sessions.filter { it.completed }.sumOf { it.completedMinutes }
            )
        } catch (e: Exception) {
            UserStats()
        }
    }
}
