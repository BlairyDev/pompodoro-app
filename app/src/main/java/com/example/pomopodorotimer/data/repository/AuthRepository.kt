package com.example.pomopodorotimer.data.repository

import com.example.pomopodorotimer.viewmodel.AuthState

interface AuthRepository {
    suspend fun loginUser(email: String, password: String): AuthState
    suspend fun signUpUser(email: String, password: String): AuthState
    fun checkAuthStatus(): AuthState
    fun getUserId(): String
    fun signOut()
}