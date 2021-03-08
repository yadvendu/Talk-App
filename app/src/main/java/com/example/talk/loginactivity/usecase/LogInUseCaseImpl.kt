package com.example.talk.loginactivity.usecase

import com.example.talk.commons.network.Result
import com.example.talk.loginactivity.repository.remote.LogInRemoteRepository
import io.reactivex.Single

class LogInUseCaseImpl(private val logInRemoteRepository: LogInRemoteRepository) : LogInUseCase {

    /**
     * This method is used to perform user sign in by passing email and password to
     * performUserSignIn inside logInRemoteRepository
     * @param email : String
     * @param password : String
     * @return : Single<Result<Boolean>>
     */
    override fun signInWithEmailAndPassword(email: String, password: String): Single<Result<Boolean>> {
        return logInRemoteRepository.performUserSignIn(email, password)
    }
}