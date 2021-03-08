package com.example.talk.commons.network

sealed class FireBaseResponse<T> {
    data class FirebaseSuccessResponse<T>(val response: T): FireBaseResponse<T>()
    data class FireBaseErrorResponse<T>(val error: T): FireBaseResponse<T>()
}