package com.example.talk.commons.di

import com.example.talk.commons.MainActivity
import com.example.talk.loginactivity.di.LoginScope
import com.example.talk.loginactivity.di.LoginModule
import com.example.talk.loginactivity.view.LoginActivity
import com.example.talk.profile.di.ProfileModule
import com.example.talk.profile.di.ProfileScope
import com.example.talk.profile.view.ProfileActivity
import com.example.talk.signup.di.SignUpModule
import com.example.talk.signup.di.SignUpScope
import com.example.talk.signup.view.SingUpActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector
    abstract fun contributesMainActivity(): MainActivity

    @LoginScope
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun contributesLoginActivity(): LoginActivity

    @SignUpScope
    @ContributesAndroidInjector(modules = [SignUpModule::class])
    abstract fun contributesSingUpActivity(): SingUpActivity

    @ProfileScope
    @ContributesAndroidInjector(modules = [ProfileModule::class])
    abstract fun contributesProfileActivity(): ProfileActivity
}