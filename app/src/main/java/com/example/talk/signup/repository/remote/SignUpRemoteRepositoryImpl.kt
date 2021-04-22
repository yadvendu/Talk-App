package com.example.talk.signup.repository.remote

import android.content.Context
import com.example.talk.commons.network.Result
import com.example.talk.commons.network.firebase.FireBaseRemoteExecutor
import com.example.talk.commons.network.firebase.FireBaseResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import io.reactivex.Single

class SignUpRemoteRepositoryImpl(private val fireBaseRemoteExecutor: FireBaseRemoteExecutor, private val firebaseUser: FirebaseAuth, private val databaseReference: DatabaseReference, private val context: Context) :
    SignUpRemoteRepository {
    override fun performUserSignUp(email: String, password: String): Single<Result<Boolean>> {
        return fireBaseRemoteExecutor.signUpUserUsingEmailAndPassword<Boolean>(email, password)
            .flatMap { result ->
                when (result) {
                    is FireBaseResponse.FirebaseSuccessResponse -> {
                        setFirebaseObject(firebaseUser, databaseReference)
                        enqueueUpdateUserName(context.applicationContext)
                        Single.just(Result.success(result.response))
                    }
                    is FireBaseResponse.FireBaseErrorResponse -> {
                        Single.just(Result.error(data = result.error, msg = "SignUpFailed Failed"))
                    }
                }
            }
    }
}