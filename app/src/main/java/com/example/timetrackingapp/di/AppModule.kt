package com.example.timetrackingapp.di

import com.example.timetrackingapp.data.repository.FirebaseAuthRepository
import com.example.timetrackingapp.data.repository.FirebaseHistoryRepository
import com.example.timetrackingapp.data.repository.FirebaseProjectRepository
import com.example.timetrackingapp.domain.repository.AuthRepository
import com.example.timetrackingapp.domain.repository.HistoryRepository
import com.example.timetrackingapp.domain.repository.ProjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository =
        FirebaseAuthRepository(auth)

    @Provides
    @Singleton
    fun provideProjectRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): ProjectRepository = FirebaseProjectRepository(firestore, auth)

    @Provides
    @Singleton
    fun provideHistoryRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): HistoryRepository = FirebaseHistoryRepository(firestore, auth)
}