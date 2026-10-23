package com.vazdautsan.drwebtesttask.domain

data class AppDetails(
    val name: String,
    val packageName: String,
    val versionName: String?,
    val versionCode: Long,
)
