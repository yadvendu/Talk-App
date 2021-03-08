package com.example.talk.loginactivity.repository.remote

import com.example.talk.commons.network.Result
import io.reactivex.Single

interface LogInRemoteRepository {
    fun performUserSignIn(email: String, password: String): Single<Result<Boolean>>
}