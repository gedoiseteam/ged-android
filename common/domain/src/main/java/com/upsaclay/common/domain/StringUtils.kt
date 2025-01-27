package com.upsaclay.common.domain

fun formatProfilePictureUrl(fileName: String, imageExtension: String): String =
    "https://objectstorage.eu-paris-1.oraclecloud.com/n/ax5bfuffglob/b/bucket-gedoise/o/$fileName.$imageExtension"