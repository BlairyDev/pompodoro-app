package com.example.pomopodorotimer.di

import com.example.pomopodorotimer.data.repository.AuthRepository
import com.example.pomopodorotimer.data.repository.AuthRepositoryReal
import com.example.pomopodorotimer.data.repository.TimerRepository
import com.example.pomopodorotimer.data.repository.TimerRepositoryReal
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepositoryReal
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTimerRepository(
        timerRepository: TimerRepositoryReal
    ): TimerRepository
}
