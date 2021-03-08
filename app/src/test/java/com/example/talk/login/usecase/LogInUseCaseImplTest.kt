package com.example.talk.login.usecase

import com.example.talk.commons.network.Result
import com.example.talk.loginactivity.repository.remote.LogInRemoteRepository
import com.example.talk.loginactivity.usecase.LogInUseCase
import com.example.talk.loginactivity.usecase.LogInUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LogInUseCaseImplTest {

    @MockK
    private lateinit var logInRemoteRepository: LogInRemoteRepository
    private lateinit var logInUseCase: LogInUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        logInUseCase = LogInUseCaseImpl(logInRemoteRepository)
    }

    @Test
    fun `given performUserSignIn enclose Success Result with data as true, when  signInWithEmailAndPassword, then return Success Result with data as true`() {
        //Given
        every { logInRemoteRepository.performUserSignIn("abc@gmail.com","abc") } returns Single.just(
            Result.success(true))

        //When
        val testObserver = logInUseCase.signInWithEmailAndPassword("abc@gmail.com","abc").test()

        //Then
        val expected = true
        Assert.assertEquals(testObserver.values()[0].data, expected)
    }

    @Test
    fun `given performUserSignIn enclose Error Result with data as false, when  signInWithEmailAndPassword, then return Success Result with data as false`() {
        //Given
        every { logInRemoteRepository.performUserSignIn("abc@gmail.com","abc") } returns Single.just(
            Result.error(msg = "",data = false))

        //When
        val testObserver = logInUseCase.signInWithEmailAndPassword("abc@gmail.com","abc").test()

        //Then
        val expected = false
        Assert.assertEquals(testObserver.values()[0].data, expected)
    }
}