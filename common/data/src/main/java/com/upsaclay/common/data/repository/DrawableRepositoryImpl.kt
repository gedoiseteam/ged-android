package com.upsaclay.common.data.repository

import android.content.Context
import android.net.Uri
import com.upsaclay.common.domain.repository.DrawableRepository

internal class DrawableRepositoryImpl(private val context: Context) : DrawableRepository {
    override fun getDrawableUri(drawableId: Int): Uri? =
        Uri.parse("android.resource://${context.packageName}/$drawableId")
}