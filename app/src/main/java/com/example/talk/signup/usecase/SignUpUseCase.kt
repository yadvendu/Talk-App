package com.example.talk.signup.usecase

import com.example.talk.commons.network.Result
import io.reactivex.Single

interface SignUpUseCase {
    fun performUserSignUp(email: String, password: String): Single<Result<Boolean>>
}