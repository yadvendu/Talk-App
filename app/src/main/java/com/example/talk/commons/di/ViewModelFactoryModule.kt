package com.example.talk.commons.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindFactory(modelsProviderFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}
