package com.example.pomopodorotimer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TimerPreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(preferences: TimerPreferencesEntity)

    @Query("SELECT * FROM timer_preferences WHERE id = 1")
    suspend fun getPreferences(): TimerPreferencesEntity
}
