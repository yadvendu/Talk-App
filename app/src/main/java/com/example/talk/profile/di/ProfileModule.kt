package com.example.talk.profile.di

import androidx.annotation.Nullable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.Module
import dagger.Provides

@Module
object ProfileModule {
    @Nullable
    @ProfileScope
    @Provides
    @JvmStatic
    fun providesFirebaseUser(firebaseAuth: FirebaseAuth): FirebaseUser? = firebaseAuth.currentUser
}
