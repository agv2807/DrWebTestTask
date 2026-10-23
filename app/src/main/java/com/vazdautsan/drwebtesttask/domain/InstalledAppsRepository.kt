package com.vazdautsan.drwebtesttask.domain

interface InstalledAppsRepository {

    suspend fun getInstalledApps(): List<InstalledApp>

    suspend fun getAppDetails(packageName: String): AppDetails

    /** SHA-256 of the app's base APK as a lowercase hex string. */
    suspend fun getApkChecksum(packageName: String): String
}
