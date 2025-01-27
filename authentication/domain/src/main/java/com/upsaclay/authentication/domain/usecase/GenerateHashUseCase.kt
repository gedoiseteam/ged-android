package com.upsaclay.authentication.domain.usecase

import java.security.MessageDigest
import java.security.SecureRandom

private const val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZaàäbc" +
        "deéèëêfghijklmnoôöpqrstuùûüµvwxyz0123456789!" +
        "@#\\\$£€%^¨{}[]&*()-_ =~+<>?/|.,;:¤"

class GenerateHashUseCase {
    private val secureRandom = SecureRandom()
    private val stringBuilder = StringBuilder()

    operator fun invoke(): String {
        val randomString = generateRandomString()
        val bytes = randomString.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun generateRandomString(): String {
        repeat(20) {
            val randomIndex = secureRandom.nextInt(chars.length)
            stringBuilder.append(chars[randomIndex])
        }
        return stringBuilder.toString()
    }
}