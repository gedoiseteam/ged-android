package com.upsaclay.common.data

import retrofit2.Response

fun <T> formatHttpError(message: String, response: Response<T>): String {
    val url = response.raw().request.url.toString()
    val method = response.raw().request.method
    val body =
        response.errorBody()?.string()?.replace(Regex("<[^>]*>"), "")?.trim() ?: "No error body"

    return """
        ERROR SERVER RESPONSE
        Request: $message
        HTTP status: ${response.code()}
        URL: $url
        Method: $method
        Body: $body
    """.trimIndent()
}

fun formatHttpError(message: String, response: okhttp3.Response): String {
    val url = response.request.url.toString()
    val method = response.request.method
    val body =
        response.body?.string()?.replace(Regex("<[^>]*>"), "")?.trim() ?: "No error body"

    return """
        ERROR SERVER RESPONSE
        Request: $message
        HTTP status: ${response.code}
        URL: $url
        Method: $method
        Body: $body
    """.trimIndent()
}