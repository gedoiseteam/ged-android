package com.upsaclay.gedoise.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.upsaclay.common.data.getGsonFlowValue
import com.upsaclay.common.data.getGsonValue
import com.upsaclay.common.data.setGsonValue
import com.upsaclay.gedoise.domain.entities.FcmToken
import kotlinx.coroutines.flow.Flow

class CredentialsDataStore(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
    private val store = context.dataStore
    private val fcmTokenKey = stringPreferencesKey("fcmTokenKey")

    fun getFcmToken(): Flow<FcmToken?> = store.getGsonFlowValue(fcmTokenKey)

    suspend fun storeFcmToken(fcmToken: FcmToken) {
        store.setGsonValue(fcmTokenKey, fcmToken)
    }

    suspend fun removeFcmToken() {
        store.edit { it.remove(fcmTokenKey) }
    }
}