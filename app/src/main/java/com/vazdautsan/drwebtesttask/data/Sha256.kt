package com.vazdautsan.drwebtesttask.data

import java.io.File
import java.security.MessageDigest

internal object Sha256 {

    private const val BUFFER_SIZE = 64 * 1024

    fun of(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString(separator = "") { "%02x".format(it) }
    }
}
