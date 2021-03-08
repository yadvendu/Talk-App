package com.example.talk.commons.network

import io.reactivex.Single

interface FireBaseRemoteExecutor {
    fun <T>authenticateUserUsingEmailAndPassword(email: String, password: String): Single<FireBaseResponse<T>>
}