package com.example.talk.signup.repository.remote

import com.example.talk.commons.network.Result
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import io.reactivex.Single

interface SignUpRemoteRepository {
    fun performUserSignUp(email: String, password: String): Single<Result<Boolean>>
}