package com.upsaclay.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import androidx.core.content.getSystemService
import com.upsaclay.common.domain.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidConnectivityObserver(context: Context): ConnectivityObserver {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    override val isConnected: Flow<Boolean>
        get() = callbackFlow {
            val callback = object : NetworkCallback() {
                override fun onUnavailable() { trySend(false) }
                override fun onAvailable(network: Network) { trySend(true) }
                override fun onLost(network: Network) { trySend(false) }
            }

            connectivityManager?.registerDefaultNetworkCallback(callback)
            awaitClose { connectivityManager?.unregisterNetworkCallback(callback) }
        }
}