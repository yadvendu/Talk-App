package com.example.talk.commons.di

import androidx.annotation.Nullable
import com.example.talk.commons.network.firebase.FireBaseRemoteExecutor
import com.example.talk.commons.network.firebase.FirebaseRemoteExecutorImpl
import com.example.talk.commons.util.FirebaseNodes
import com.example.talk.profile.di.ProfileScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
    fun providesFirebaseRemoteExecutor(firebaseAuth: FirebaseAuth): FireBaseRemoteExecutor =
        FirebaseRemoteExecutorImpl(
            firebaseAuth
        )

    @Singleton
    @Provides
    @JvmStatic
    fun providesDatabaseReference(): DatabaseReference = FirebaseDatabase.getInstance().reference.child(
        FirebaseNodes.users)
}