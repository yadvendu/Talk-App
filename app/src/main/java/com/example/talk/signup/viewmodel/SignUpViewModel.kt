package com.example.talk.signup.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.talk.commons.Event
import com.example.talk.commons.network.Result
import com.example.talk.signup.usecase.SignUpUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
): ViewModel() {

    private var _viewEvents = MutableLiveData<Event<SignUpViewEvents>>()
    var viewEvents: LiveData<Event<SignUpViewEvents>> = _viewEvents

    private val _viewState: MutableLiveData<SignUpViewState> = MutableLiveData()
    val viewState: LiveData<SignUpViewState> = _viewState

    init {
        _viewState.value = SignUpViewState(isLoading = false)
    }

    fun performUserSignUp(email: String, password: String, confirmPassword: String, name: String) {
        setIsLoadingState(true)
        when {
            email.isEmpty() -> {
                _viewEvents.value = Event(SignUpViewEvents.SignUpValidation(isEmailEmpty = true))
            }
            password.isEmpty() -> {
                _viewEvents.value = Event(SignUpViewEvents.SignUpValidation(isPasswordEmpty = true))
            }
            confirmPassword.isEmpty() -> {
                _viewEvents.value = Event(SignUpViewEvents.SignUpValidation(isConfirmPasswordEmpty = true))
            }
            password != confirmPassword -> {
                _viewEvents.value = Event(SignUpViewEvents.SignUpValidation(isPasswordMismatch = true))
            }
            name.isEmpty() -> {
                _viewEvents.value = Event(SignUpViewEvents.SignUpValidation(isNameEmpty = true))
            }
            else -> {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _viewEvents.value = Event(SignUpViewEvents.SignUpValidation(isEmailNotValid = true))
                } else {
                    signUpUser(email, password)
                }
            }
        }
    }

    private fun signUpUser(email: String, password: String) {
        val disposable = signUpUseCase.performUserSignUp(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::processSignUpResponse)
    }

    private fun processSignUpResponse(response: Result<Boolean>) {
        setIsLoadingState(false)
        when(response.status) {
             Result.Status.SUCCESS -> {
                 _viewEvents.value = Event(SignUpViewEvents.SignUpStatus(true))
             }
            Result.Status.ERROR -> {
                _viewEvents.value = Event(SignUpViewEvents.SignUpStatus(false))
            }
        }
    }

    /**
     * This method is used to set isLoading view state
     * @param isLoading : Boolean
     */
    private fun setIsLoadingState(isLoading: Boolean) {
        val newViewState = viewState.value?.copy(isLoading = isLoading)
        setNewViewState(newViewState)
    }

    private fun setNewViewState(signUpViewState: SignUpViewState?) {
        signUpViewState?.let {
            _viewState.value = it
        }
    }
}

//val firebaseAuth = FirebaseAuth.getInstance()
//firebaseAuth.createUserWithEmailAndPassword(email, password)
//.addOnCompleteListener {
//    if (it.isSuccessful) {
//        firebaseUser = firebaseAuth.currentUser
//        localImageUri?.let { imageUri ->
//            updateNameAndPhoto(imageUri)
//        } ?: updateNameOnly()
//        updateNameOnly()
//    } else {
//        Toast.makeText(
//            this@SingUpActivity,
//            getString(R.string.sign_up_failed),
//            Toast.LENGTH_SHORT
//        ).show()
//    }
//}