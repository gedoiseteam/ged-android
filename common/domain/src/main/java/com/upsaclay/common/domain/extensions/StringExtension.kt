package com.upsaclay.common.domain.extensions


fun String.uppercaseFirstLetter(): String = this.replaceFirstChar { it.uppercase() }