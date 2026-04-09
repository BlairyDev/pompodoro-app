package com.example.pomopodorotimer.model

data class AppUser(
    val uid: String = "",
    val email: String = "",
    val createdAt: Long = 0L,
    val defaultSessionMinutes: Long = 25
)
