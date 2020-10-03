package com.marchlab.haema.data.repository

import com.marchlab.haema.data.datasource.AppLockDataSource
import com.marchlab.haema.domain.repository.AppLockRepository

class AppLockRepositoryImpl(
    private val appLockDataSource: AppLockDataSource
) : AppLockRepository {
    override fun setPassword(password: String) {
        appLockDataSource.setPassword(password)
    }

    override fun removePassword() {
        appLockDataSource.removePassword()
    }

    override fun getPassword(): String? {
        return appLockDataSource.getPassword()
    }

    override fun checkAppLock(): Boolean {
        return appLockDataSource.checkAppLock()
    }

}