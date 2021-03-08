package com.example.talk.loginactivity.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.talk.R
import com.example.talk.commons.Event
import com.example.talk.commons.TalkBaseActivity
import com.example.talk.databinding.ActivityLoginBinding
import com.example.talk.commons.di.ViewModelProviderFactory
import com.example.talk.loginactivity.viewmodel.LoginViewEvents
import com.example.talk.loginactivity.viewmodel.LoginViewModel
import com.example.talk.signup.view.SingUpActivity
import javax.inject.Inject

class LoginActivity : TalkBaseActivity<ActivityLoginBinding>() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindView(R.layout.activity_login)
        loginViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)

        with(binding) {
            lifecycleOwner = this@LoginActivity
            logInActivity = this@LoginActivity
            viewModel = loginViewModel
        }

        observeViewEvent()
        observeViewState()
    }

    /**
     * This method is used to observe login view events
     */
    private fun observeViewEvent() {
        loginViewModel.viewEvents.observe(this, Observer {
            handleViewEvents(it)
        })
    }

    /**
     * This method is used to open sing up activity
     */
    fun openSingUpActivity() {
        val intent = Intent(this, SingUpActivity::class.java)
        startActivity(intent)
    }

    /**
     * This method send email and password entered by user
     * to perform user login with
     */
    fun executeUserLogin() {
        with(binding) {
            val email = emailTextView.text?.trim().toString()
            val password = passwordTextView.text?.trim().toString()
            loginViewModel.performUserLogin(email, password)
        }
    }

    /**
     * This method is used to handle login view events
     * @param events : Event<LoginViewEvents>
     */
    private fun handleViewEvents(events: Event<LoginViewEvents>) {
        events.getContentIfNotHandled()?.let {
            when (it) {
                is LoginViewEvents.OpenHomeScreen -> {
                    if (it.isLoggedIn) {
                        Toast.makeText(this, "Logged In Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Logged In Failed", Toast.LENGTH_SHORT).show()
                    }
                }
                is LoginViewEvents.LogInValidation -> {
                    with(binding) {
                        when {
                            it.isEmailEmpty -> {
                                emailTextView.error = getString(R.string.enter_email_hint)
                            }
                            it.isEmailNotValid -> {
                                emailTextView.error = getString(R.string.invalid_email_error)
                            }
                            else -> {
                                passwordTextView.error = getString(R.string.enter_password_hint)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method is used to observe login view state
     */
    private fun observeViewState() {
        loginViewModel.viewState.observe(this, Observer {
            //Do  something on observing view state
        })
    }
}
