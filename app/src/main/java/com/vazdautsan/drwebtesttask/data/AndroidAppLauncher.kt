package com.vazdautsan.drwebtesttask.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.vazdautsan.drwebtesttask.domain.AppLauncher

class AndroidAppLauncher(private val context: Context) : AppLauncher {

    override fun canLaunch(packageName: String): Boolean =
        context.packageManager.getLaunchIntentForPackage(packageName) != null

    override fun launch(packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }
}
