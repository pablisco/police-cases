package com.pablisco.crimeapp.test

import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

/**
 * alias for [ArgumentCaptor#forClass]
 */
inline fun <reified T> captor() =
    ArgumentCaptor.forClass(T::class.java) ?: throw RuntimeException("Could not create captor for ${T::class}")

/**
 * Useful to create mocks in one place
 */
inline fun <reified T> mock(f: T.() -> Unit) =
    Mockito.mock(T::class.java)?.also(f) ?: throw RuntimeException("Could not create mock for ${T::class}")

/**
 * Alias for [Mockito#when]
 */
inline fun <reified T> on(t: T): OngoingStubbing<T> = Mockito.`when`(t)

/**
 * Mockito returns a null with captures and Kotlin is not happy about this.
 */
fun <T> ArgumentCaptor<T>.safeCapture(): T {
    this.capture()
    return uninitialized()
}

/**
 * Mockito returns a null with captures and Kotlin is not happy about this.
 */
fun <T> notNull(): T {
    Mockito.notNull<T>()
    return uninitialized()
}

/**
 * For when one just wants to spy on what a mock is been given
 */
fun <T> OngoingStubbing<T>.thenDoNothing(): OngoingStubbing<T> = this.then { /* do nothing */ }

/**
 * Creates a nullable instance, not a null object, so that Kotlin is happy with what mockito returns
 */
@Suppress("UNCHECKED_CAST")
private fun <T> uninitialized(): T = null as T