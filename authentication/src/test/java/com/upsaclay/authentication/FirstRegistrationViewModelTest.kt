package com.upsaclay.authentication

import com.upsaclay.authentication.presentation.viewmodels.FirstRegistrationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class FirstRegistrationViewModelTest {
    private lateinit var firstRegistrationViewModel: FirstRegistrationViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val firstName = "John"
    private val lastName = "Doe"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        firstRegistrationViewModel = FirstRegistrationViewModel()
    }

    @Test
    fun default_values_are_correct() {
        // Then
        assertEquals("", firstRegistrationViewModel.firstName.value)
        assertEquals("", firstRegistrationViewModel.lastName.value)
    }

    @Test
    fun updateFirstName_should_update_firstName() {
        // When
        firstRegistrationViewModel.updateFirstName(firstName)

        // Then
        assertEquals(firstName, firstRegistrationViewModel.firstName.value)
    }

    @Test
    fun updateLastName_should_update_lastName() {
        // When
        firstRegistrationViewModel.updateLastName(lastName)

        // Then
        assertEquals(lastName, firstRegistrationViewModel.lastName.value)
    }

    @Test
    fun verifyNamesInputs_should_return_true_when_names_are_not_empty() {
        // Given
        firstRegistrationViewModel.updateFirstName(firstName)
        firstRegistrationViewModel.updateLastName(lastName)

        // When
        val result = firstRegistrationViewModel.verifyNamesInputs()

        // Then
        assertEquals(true, result)
    }

    @Test
    fun verifyNamesInputs_should_return_false_when_names_are_empty() {
        // When
        val result = firstRegistrationViewModel.verifyNamesInputs()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun verifyNamesInputs_should_return_false_when_names_are_blank() {
        // Given
        firstRegistrationViewModel.updateFirstName("  ")
        firstRegistrationViewModel.updateLastName("")

        // When
        val result = firstRegistrationViewModel.verifyNamesInputs()

        // Then
        assertFalse(result)
    }

    @Test
    fun verifyNamesInputs_should_return_true_when_names_are_not_blank() {
        // Given
        firstRegistrationViewModel.updateFirstName("  John  ")
        firstRegistrationViewModel.updateLastName("  Doe  ")

        // When
        val result = firstRegistrationViewModel.verifyNamesInputs()

        // Then
        assertEquals(true, result)
    }

    @Test
    fun correctNamesInputs_should_trim_and_uppercase_names() {
        // Given
        firstRegistrationViewModel.updateFirstName("john ")
        firstRegistrationViewModel.updateLastName(" doe")

        // When
        firstRegistrationViewModel.correctNamesInputs()

        // Then
        assertEquals(firstName, firstRegistrationViewModel.firstName.value)
        assertEquals(lastName, firstRegistrationViewModel.lastName.value)
    }
}