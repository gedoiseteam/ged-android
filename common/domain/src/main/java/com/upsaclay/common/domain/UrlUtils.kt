package com.upsaclay.common.domain

object UrlUtils {
    fun formatProfilePictureUrl(fileName: String?): String? {
        if (fileName == null) return null

        return "https://objectstorage.eu-paris-1.oraclecloud.com/n/ax5bfuffglob/b/bucket-gedoise/o/$fileName"
    }

    fun getFileNameFromUrl(url: String?): String? = url?.substringAfterLast("/")
}