package com.vazdautsan.drwebtesttask.domain

interface AppLauncher {

    fun canLaunch(packageName: String): Boolean

    /** @return false if the app has no launchable activity or failed to start. */
    fun launch(packageName: String): Boolean
}
