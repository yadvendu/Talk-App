package com.example.talk.signup.repository.remote

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.RxWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.talk.commons.util.FirebaseNodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UpdateUserName(appContext: Context, workParams: WorkerParameters) :
    RxWorker(appContext, workParams) {
    override fun createWork(): Single<Result> {
        return makeApiCall()
            .subscribeOn(Schedulers.io())
            .map {
                Result.success()
            }
    }

    private fun makeApiCall(): Single<Result> {
        val userProfileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName("Yadu")
            .build()
        val firebaseUser = firebaseAuth.currentUser
        return Single.create { emitter ->
            firebaseUser?.updateProfile(userProfileChangeRequest)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val userId = firebaseUser.uid
                    val userData = createUser()
                    userId.let { userIdPath ->
                        databaseReference.child(userIdPath).setValue(userData)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    emitter.onSuccess(Result.success())
                                } else {
                                    emitter.onSuccess(Result.failure())
                                }
                            }
                    }
                }
            }
        }
    }

    private fun createUser(): HashMap<String, String> {
        val userHashMap = HashMap<String, String>()
        userHashMap[FirebaseNodes.name] = "yadvendu"
        userHashMap[FirebaseNodes.email] = "yadvendu@gmail.com"
        userHashMap[FirebaseNodes.photo] = ""
        userHashMap[FirebaseNodes.online] = "true"
        return userHashMap
    }

}


private lateinit var firebaseAuth: FirebaseAuth
private lateinit var databaseReference: DatabaseReference

fun setFirebaseObject(firebaseAuthentication: FirebaseAuth, databaseRef: DatabaseReference) {
    firebaseAuth = firebaseAuthentication
    databaseReference = databaseRef
}

fun enqueueUpdateUserName(appContext: Context) {
    val workRequest = OneTimeWorkRequest.Builder(UpdateUserName::class.java)
        .addTag("UpdateUserName")
        .build()
    cancelScheduledWorker(appContext)
    WorkManager.getInstance(appContext).enqueue(workRequest)
}

fun cancelScheduledWorker(appContext: Context) {
    WorkManager.getInstance(appContext).cancelAllWorkByTag("UpdateUserName")
}