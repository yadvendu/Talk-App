package com.example.talk.loginactivity.usecase

import com.example.talk.commons.network.Result
import io.reactivex.Single

interface LogInUseCase {
    fun signInWithEmailAndPassword(email: String, password:String): Single<Result<Boolean>>
}