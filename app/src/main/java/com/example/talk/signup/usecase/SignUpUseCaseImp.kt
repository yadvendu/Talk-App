package com.example.talk.signup.usecase

import com.example.talk.commons.network.Result
import com.example.talk.signup.repository.remote.SignUpRemoteRepository
import io.reactivex.Single

class SignUpUseCaseImp(private val signUpRemoteRepository: SignUpRemoteRepository): SignUpUseCase {
    override fun performUserSignUp(email: String, password: String): Single<Result<Boolean>> {
        return signUpRemoteRepository.performUserSignUp(email, password)
    }
}