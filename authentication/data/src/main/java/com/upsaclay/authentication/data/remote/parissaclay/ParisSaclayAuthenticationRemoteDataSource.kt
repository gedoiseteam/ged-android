package com.upsaclay.authentication.data.remote.parissaclay

import com.upsaclay.common.data.formatHttpError
import com.upsaclay.common.domain.e
import com.upsaclay.common.domain.i
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

internal class ParisSaclayAuthenticationRemoteDataSource(
    private val authenticationRetrofitApi: AuthenticationRetrofitApi
) {
    suspend fun loginWithParisSaclay(email: String, password: String, hash: String) {
        withContext(Dispatchers.IO) {
            i("Logging in with Paris-Saclay...")
            val response = authenticationRetrofitApi.login(email, password, hash)
            if (!response.isSuccessful) {
                val errorMessage = formatHttpError("Error logging in with Paris-Saclay", response)
                e(errorMessage)
                throw IOException(errorMessage)
            }
        }
    }
}