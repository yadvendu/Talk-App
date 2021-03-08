package com.example.talk.login.viewmodel

import android.util.Patterns
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.talk.commons.Event
import com.example.talk.commons.RxImmediateSchedulerRule
import com.example.talk.commons.network.Result
import com.example.talk.loginactivity.usecase.LogInUseCase
import com.example.talk.loginactivity.viewmodel.LoginViewEvents
import com.example.talk.loginactivity.viewmodel.LoginViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import org.junit.*
import org.junit.Assert.*
import org.mockito.*
import org.mockito.Mockito.*
import java.util.regex.Pattern

class LoginViewModelTest {

    @get:Rule
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    val ruleForLivaData = InstantTaskExecutorRule()

    @Captor
    private lateinit var loginViewEventCaptor: ArgumentCaptor<Event<LoginViewEvents>>

    @Mock
    private lateinit var viewEventTestObserver: Observer<Event<LoginViewEvents>>

    @MockK
    private lateinit var logInUseCase: LogInUseCase

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `given empty email, when performUserLogin then view event is LogInValidation`() {
        loginViewModel = LoginViewModel(logInUseCase)
        loginViewModel.viewEvents.observeForever(viewEventTestObserver)
        val email = ""
        val password = "abc"
        loginViewModel.performUserLogin(email.trim().toString(),password.trim().toString())

        with(loginViewEventCaptor) {
            verify(viewEventTestObserver, times(1)).onChanged(capture())
            val viewEvents = allValues[0].getContentIfNotHandled()
            assertTrue(viewEvents is LoginViewEvents.LogInValidation)
            assertEquals(true, (viewEvents as LoginViewEvents.LogInValidation).isEmailEmpty)
            assertEquals(false, (viewEvents as LoginViewEvents.LogInValidation).isPasswordEmpty)
            assertEquals(false, (viewEvents as LoginViewEvents.LogInValidation).isEmailNotValid)
        }
    }

    @Test
    fun `given empty password, when performUserLogin then view event is LogInValidation`() {
        //Given
        loginViewModel = LoginViewModel(logInUseCase)
        loginViewModel.viewEvents.observeForever(viewEventTestObserver)
        val email = "abc@gmail.com"
        val password = ""
        loginViewModel.performUserLogin(email.trim().toString(),password.trim().toString())

        with(loginViewEventCaptor) {
            verify(viewEventTestObserver, times(1)).onChanged(capture())
            val viewEvents = allValues[0].getContentIfNotHandled()
            assertTrue(viewEvents is LoginViewEvents.LogInValidation)
            assertEquals(false, (viewEvents as LoginViewEvents.LogInValidation).isEmailEmpty)
            assertEquals(true, (viewEvents as LoginViewEvents.LogInValidation).isPasswordEmpty)
            assertEquals(false, (viewEvents as LoginViewEvents.LogInValidation).isEmailNotValid)
        }
    }


    @Test
    fun `given email and password with successful login encloses success response, when performUserLogin then view event is OpenHomeScreen`() {
        //Given
        loginViewModel = LoginViewModel(logInUseCase)
        loginViewModel.viewEvents.observeForever(viewEventTestObserver)
        val email = "abc@gmail.com"
        val password = "abc123"

        every { logInUseCase.signInWithEmailAndPassword(email.trim().toString(),password.trim().toString()) } returns Single.just(
            Result.success(true))

        //When
        loginViewModel.performUserLogin(email.trim().toString(),password.trim().toString())

        //Then
        with(loginViewEventCaptor) {
            verify(viewEventTestObserver, times(1)).onChanged(capture())
            val viewEvents = allValues[0].getContentIfNotHandled()
            assertTrue(viewEvents is LoginViewEvents.OpenHomeScreen)
            assertEquals(true, (viewEvents as LoginViewEvents.OpenHomeScreen).isLoggedIn)
        }
    }

    @Test
    fun `given email and password with failed login encloses failure response, when performUserLogin then view event is OpenHomeScreen`() {
        loginViewModel = LoginViewModel(logInUseCase)
        loginViewModel.viewEvents.observeForever(viewEventTestObserver)
        val email = "abc@gmail.com"
        val password = "abc123"

        every { logInUseCase.signInWithEmailAndPassword(email.trim().toString(),password.trim().toString()) } returns Single.just(
            Result.error("Authentication Failed",false))

        loginViewModel.performUserLogin(email.trim().toString(),password.trim().toString())

        with(loginViewEventCaptor) {
            verify(viewEventTestObserver, times(1)).onChanged(capture())
            val viewEvents = allValues[0].getContentIfNotHandled()
            assertTrue(viewEvents is LoginViewEvents.OpenHomeScreen)
            assertEquals(false, (viewEvents as LoginViewEvents.OpenHomeScreen).isLoggedIn)
        }
    }

    @After
    fun tearDown() {
        loginViewModel.viewEvents.removeObserver(viewEventTestObserver)
    }

}