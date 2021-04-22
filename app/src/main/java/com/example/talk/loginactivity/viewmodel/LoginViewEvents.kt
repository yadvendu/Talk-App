package com.example.talk.loginactivity.viewmodel

sealed class LoginViewEvents {
    data class OpenHomeScreen(val isLoggedIn: Boolean) : LoginViewEvents()
    data class LogInValidation(
        val isNameEmpty: Boolean = false,
        val isEmailEmpty: Boolean = false,
        val isPasswordEmpty: Boolean = false,
        val isConfirmPasswordEmpty: Boolean = false,
        val isPasswordMismatch: Boolean = false,
        val isEmailNotValid: Boolean = false
    ) : LoginViewEvents()
}
