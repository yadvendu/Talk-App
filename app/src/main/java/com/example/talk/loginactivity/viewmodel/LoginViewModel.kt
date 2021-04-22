package com.example.talk.loginactivity.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.talk.commons.Event
import com.example.talk.commons.network.Result
import com.example.talk.commons.util.exhaustive
import com.example.talk.loginactivity.usecase.LogInUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val logInUseCase: LogInUseCase) : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
        private const val LOGIN_SUCCESS_TAG = "Login Success"
        private const val LOGIN_FAILURE_TAG = "Login Failed"
    }

    private val _viewEvents: MutableLiveData<Event<LoginViewEvents>> = MutableLiveData()
    val viewEvents: LiveData<Event<LoginViewEvents>> = _viewEvents

    private val _viewState: MutableLiveData<LoginViewState> = MutableLiveData()
    val viewState: LiveData<LoginViewState> = _viewState

    init {
        _viewState.value = LoginViewState(isLoading = false)
    }

    /**
     * This method is used to perform user login
     * It validates first if email and password and not empty
     * If validation is successful it performs user login
     * @param email : String
     * @param password : String
     */
    fun performUserLogin(email: String, password: String) {
        when {
            email.isEmpty() -> {
                _viewEvents.value = Event(LoginViewEvents.LogInValidation(isEmailEmpty = true))
            }
            password.isEmpty() -> {
                _viewEvents.value = Event(LoginViewEvents.LogInValidation(isPasswordEmpty = true))
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _viewEvents.value = Event(LoginViewEvents.LogInValidation(isEmailNotValid = true))
            }
            else -> {
                setIsLoadingState(true)
                logInUseCase.signInWithEmailAndPassword(email, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::processLoginResponse)
            }
        }
    }

    /**
     * This method process login response and
     * set require view events
     * @param response : Result<Boolean>
     */
    private fun processLoginResponse(response: Result<Boolean>) {
        setIsLoadingState(false)
        when(response.status) {
            Result.Status.SUCCESS -> {
                Log.d(TAG,response.message?: LOGIN_SUCCESS_TAG)
                _viewEvents.value = Event(LoginViewEvents.OpenHomeScreen(true))
            }

            Result.Status.ERROR -> {
                Log.d(TAG,response.message?: LOGIN_FAILURE_TAG)
                _viewEvents.value = Event(LoginViewEvents.OpenHomeScreen(false))
            }
        }.exhaustive
    }

    /**
     * This method is used to set isLoading view state
     * @param isLoading : Boolean
     */
    private fun setIsLoadingState(isLoading: Boolean) {
        val newViewState = viewState.value?.copy(isLoading = isLoading)
        setNewViewState(newViewState)
    }

    /**
     * This method is used to set new view state
     * @param loginViewState: LoginViewState
     */
    private fun setNewViewState(loginViewState: LoginViewState?) {
        loginViewState?.let {
            _viewState.value = it
        }
    }
}