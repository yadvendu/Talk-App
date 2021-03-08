package com.example.talk.commons

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

fun <T> anyNotNull(): T {
    Mockito.any<T>()
    return uninitialized()
}
private fun <T> uninitialized(): T = null as T

fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T =
    argumentCaptor.capture()

inline fun <reified T> lambdaMock(): T = Mockito.mock(T::class.java)