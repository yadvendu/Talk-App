package com.example.talk.loginactivity.repository.remote

import com.example.talk.commons.network.firebase.FireBaseRemoteExecutor
import com.example.talk.commons.network.firebase.FireBaseResponse
import com.example.talk.commons.network.Result
import com.example.talk.commons.util.exhaustive
import io.reactivex.Single

class LogInRemoteRepositoryImpl(private val fireBaseRemoteExecutor: FireBaseRemoteExecutor) :
    LogInRemoteRepository {

    /**
     * This method performs actual login using email and password by
     * calling firebase executor
     * It receives the firebase response and return appropriate Result
     *
     * @param email: String
     * @param password: String
     * @return : Single<Result<Boolean>>
     */
    override fun performUserSignIn(email: String, password: String): Single<Result<Boolean>> {
        return fireBaseRemoteExecutor.authenticateUserUsingEmailAndPassword<Boolean>(email, password)
            .flatMap { result ->
                when (result) {
                    is FireBaseResponse.FirebaseSuccessResponse -> {
                        Single.just(Result.success(result.response))
                    }
                    is FireBaseResponse.FireBaseErrorResponse -> {
                        Single.just(Result.error(data = result.error, msg = "Authentication Failed"))
                    }
                }.exhaustive
            }
    }
}