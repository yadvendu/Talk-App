package com.example.talk.login.repository.remote

import com.example.talk.commons.network.FireBaseRemoteExecutor
import com.example.talk.commons.network.FireBaseResponse
import com.example.talk.loginactivity.repository.remote.LogInRemoteRepository
import com.example.talk.loginactivity.repository.remote.LogInRemoteRepositoryImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LogInRemoteRepoImplTest {

    @MockK
    private lateinit var fireBaseRemoteExecutor: FireBaseRemoteExecutor
    private lateinit var logInRemoteRepository: LogInRemoteRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        logInRemoteRepository = LogInRemoteRepositoryImpl(fireBaseRemoteExecutor)
    }

    @Test
    fun `given authenticateUserUsingEmailAndPassword encloses Firebase Success Response with true boolean value, when performUserSignIn, then return Result with data as true`() {
        //Given
        every { fireBaseRemoteExecutor.authenticateUserUsingEmailAndPassword<Boolean>("abc@gmail.com", "abc123") } returns Single.just(FireBaseResponse.FirebaseSuccessResponse(true))

        //When
        val testObserver = logInRemoteRepository.performUserSignIn("abc@gmail.com", "abc123").test()

        //Then
        val expectedResult = true
        Assert.assertEquals(testObserver.values()[0].data, expectedResult)
    }

    @Test
    fun `given authenticateUserUsingEmailAndPassword encloses Firebase Error Response with false boolean value, when performUserSignIn, then return Result with data as false`() {
        //Given
        every { fireBaseRemoteExecutor.authenticateUserUsingEmailAndPassword<Boolean>("abc@gmail.com", "abc123") } returns Single.just(FireBaseResponse.FireBaseErrorResponse(false))

        //When
        val testObserver = logInRemoteRepository.performUserSignIn("abc@gmail.com", "abc123").test()

        //Then
        val expectedResult = false
        Assert.assertEquals(testObserver.values()[0].data, expectedResult)
    }
}