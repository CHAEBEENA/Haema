package com.marchlab.haema.data.datasource

import com.marchlab.haema.data.preference.PreferenceStorage

interface AppLockDataSource {

    fun setPassword(password: String)

    fun removePassword()

    fun getPassword(): String?

    fun checkAppLock(): Boolean
}

class AppLockDataSourceImpl(
    private val preferenceStorage: PreferenceStorage
): AppLockDataSource {

    override fun setPassword(password: String) {
        preferenceStorage.password = password
        preferenceStorage.appLock = true
    }

    override fun removePassword() {
        preferenceStorage.password = ""
        preferenceStorage.appLock = false
    }

    override fun getPassword(): String? = preferenceStorage.password

    override fun checkAppLock(): Boolean = preferenceStorage.appLock
}