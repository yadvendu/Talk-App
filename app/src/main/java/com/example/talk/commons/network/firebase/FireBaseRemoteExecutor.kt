package com.example.talk.commons.network.firebase

import io.reactivex.Single

interface FireBaseRemoteExecutor {
    fun <T>authenticateUserUsingEmailAndPassword(email: String, password: String): Single<FireBaseResponse<T>>

    fun <T>signUpUserUsingEmailAndPassword(email: String, password: String): Single<FireBaseResponse<T>>
}