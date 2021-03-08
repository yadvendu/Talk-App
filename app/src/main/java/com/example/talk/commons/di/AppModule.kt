package com.example.talk.commons.di

import com.example.talk.commons.network.FireBaseRemoteExecutor
import com.example.talk.commons.network.FirebaseRemoteExecutorImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {
    @Singleton
    @Provides
    @JvmStatic
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    @JvmStatic
    fun providesFirebaseRemoteExecutor(firebaseAuth: FirebaseAuth): FireBaseRemoteExecutor = FirebaseRemoteExecutorImpl(firebaseAuth)
}