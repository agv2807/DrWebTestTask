package com.vazdautsan.drwebtesttask.data

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import com.vazdautsan.drwebtesttask.domain.AppDetails
import com.vazdautsan.drwebtesttask.domain.InstalledApp
import com.vazdautsan.drwebtesttask.domain.InstalledAppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class InstalledAppsRepositoryImpl(context: Context) : InstalledAppsRepository {

    private val packageManager = context.packageManager

    override suspend fun getInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        packageManager.installedApplications()
            .map { info ->
                InstalledApp(
                    name = info.loadLabel(packageManager).toString(),
                    packageName = info.packageName,
                )
            }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }

    override suspend fun getAppDetails(packageName: String): AppDetails = withContext(Dispatchers.IO) {
        val packageInfo = packageManager.packageInfo(packageName)
        AppDetails(
            name = packageInfo.applicationInfo?.loadLabel(packageManager)?.toString() ?: packageName,
            packageName = packageName,
            versionName = packageInfo.versionName,
            versionCode = PackageInfoCompat.getLongVersionCode(packageInfo),
        )
    }

    override suspend fun getApkChecksum(packageName: String): String = withContext(Dispatchers.IO) {
        val apkPath = checkNotNull(packageManager.packageInfo(packageName).applicationInfo).sourceDir
        Sha256.of(File(apkPath))
    }
}
