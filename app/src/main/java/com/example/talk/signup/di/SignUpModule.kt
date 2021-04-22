package com.example.talk.signup.di

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.talk.commons.di.ViewModelKey
import com.example.talk.commons.network.firebase.FireBaseRemoteExecutor
import com.example.talk.signup.repository.remote.SignUpRemoteRepository
import com.example.talk.signup.repository.remote.SignUpRemoteRepositoryImpl
import com.example.talk.signup.usecase.SignUpUseCase
import com.example.talk.signup.usecase.SignUpUseCaseImp
import com.example.talk.signup.viewmodel.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
object SignUpModule {

    @SignUpScope
    @Provides
    @JvmStatic
    fun providesSignUpRemoteRepository(fireBaseRemoteExecutor: FireBaseRemoteExecutor, firebaseAuth: FirebaseAuth, databaseReference: DatabaseReference, application: Application): SignUpRemoteRepository = SignUpRemoteRepositoryImpl(fireBaseRemoteExecutor, firebaseAuth, databaseReference, application)

    @SignUpScope
    @Provides
    @JvmStatic
    fun providesSignUpUseCase(signUpRemoteRepository: SignUpRemoteRepository): SignUpUseCase = SignUpUseCaseImp(signUpRemoteRepository)

    @Provides
    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    @JvmStatic
    fun providesSignUpViewModel(signUpUseCase: SignUpUseCase): ViewModel = SignUpViewModel(signUpUseCase)
}