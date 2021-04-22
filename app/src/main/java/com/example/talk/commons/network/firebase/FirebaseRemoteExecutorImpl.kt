package com.example.talk.commons.network.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import io.reactivex.Single

@Suppress("UNCHECKED_CAST")
class FirebaseRemoteExecutorImpl(
    private val firebaseAuth: FirebaseAuth
) : FireBaseRemoteExecutor {

    /**
     * This method is used for user authentication from firebase using email and password
     * It accepts a generic type of 'T' and return the same generic type as FirebaseSuccessResponse
     * or FireBaseErrorResponse
     * @param email: String
     * @param password: String
     * @return : Single<FireBaseResponse<T>>
     */
    override fun<T> authenticateUserUsingEmailAndPassword(
        email: String,
        password: String
    ): Single<FireBaseResponse<T>> {
        return Single.create { emitter ->
            firebaseAuth.signInWithEmailAndPassword(
                email,
                password
            )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onSuccess(
                            FireBaseResponse.FirebaseSuccessResponse(
                                it.isSuccessful as T
                            )
                        )
                    } else {
                        emitter.onSuccess(
                            FireBaseResponse.FireBaseErrorResponse(
                                it.isSuccessful as T
                            )
                        )
                    }
                }
        }
    }

    override fun <T> signUpUserUsingEmailAndPassword(
        email: String,
        password: String
    ): Single<FireBaseResponse<T>> {
        return Single.create { emitter ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onSuccess(
                            FireBaseResponse.FirebaseSuccessResponse(
                                it.isSuccessful as T
                            )
                        )
                    } else {
                        emitter.onSuccess(
                            FireBaseResponse.FireBaseErrorResponse(
                                it.isSuccessful as T
                            )
                        )
                    }
                }
        }
    }
}
