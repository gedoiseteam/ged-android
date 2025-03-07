package com.upsaclay.common.domain

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

fun Any.d(message: String) {
    Timber.tag(javaClass.simpleName).d(message)
}

fun Any.e(message: String, throwable: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        Timber.tag(javaClass.simpleName).e(throwable, message)
    } else {
        throwable?.let { FirebaseCrashlytics.getInstance().recordException(throwable) }
        Timber.tag(javaClass.simpleName).e(throwable, message)
    }
}

fun Any.i(message: String) {
    Timber.tag(javaClass.simpleName).i(message)
}

fun Any.w(message: String) {
    Timber.tag(javaClass.simpleName).w(message)
}