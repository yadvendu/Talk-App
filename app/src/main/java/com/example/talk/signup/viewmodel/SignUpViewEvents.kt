package com.example.talk.signup.viewmodel

sealed class SignUpViewEvents {
    data class SignUpValidation(
        val isEmailEmpty: Boolean = false,
        val isPasswordEmpty: Boolean = false,
        val isEmailNotValid: Boolean = false,
        val isConfirmPasswordEmpty: Boolean = false,
        val isPasswordMismatch: Boolean = false,
        val isNameEmpty: Boolean = false
    ): SignUpViewEvents()
    data class SignUpStatus(val isSignUpSuccessFull: Boolean):SignUpViewEvents()
}