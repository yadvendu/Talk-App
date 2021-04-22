package com.example.talk.loginactivity.di

import androidx.lifecycle.ViewModel
import com.example.talk.commons.di.ViewModelKey
import com.example.talk.commons.network.firebase.FireBaseRemoteExecutor
import com.example.talk.loginactivity.repository.remote.LogInRemoteRepository
import com.example.talk.loginactivity.repository.remote.LogInRemoteRepositoryImpl
import com.example.talk.loginactivity.usecase.LogInUseCase
import com.example.talk.loginactivity.usecase.LogInUseCaseImpl
import com.example.talk.loginactivity.viewmodel.LoginViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
object LoginModule {
    @LoginScope
    @Provides
    @JvmStatic
    fun providesLogInRemoteRepository(fireBaseRemoteExecutor: FireBaseRemoteExecutor): LogInRemoteRepository = LogInRemoteRepositoryImpl(fireBaseRemoteExecutor)

    @LoginScope
    @Provides
    @JvmStatic
    fun providesLogInUseCase(logInRemoteRepository: LogInRemoteRepository): LogInUseCase = LogInUseCaseImpl(logInRemoteRepository)

    @Provides
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    @JvmStatic
    fun providesLoginViewModel(logInUseCase: LogInUseCase): ViewModel = LoginViewModel(logInUseCase)
}