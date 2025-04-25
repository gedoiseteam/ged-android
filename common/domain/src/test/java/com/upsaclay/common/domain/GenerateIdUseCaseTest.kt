package com.upsaclay.common.domain

import com.upsaclay.common.domain.usecase.GenerateIdUseCase
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotEquals

class GenerateIdUseCaseTest {
    private lateinit var generateIdUseCase: GenerateIdUseCase

    @Before
    fun setUp() {
        generateIdUseCase = GenerateIdUseCase
    }

    @Test
    fun generateIdUseCase_should_generate_unique_id() {
        for (i in 0..100) {
            // When
            val id1 = generateIdUseCase.asString()
            val id2 = generateIdUseCase.asString()

            // Then
            assertNotEquals(id1, id2)
        }
    }
}