package com.marchlab.haema.domain.repository

interface AppLockRepository {

    fun setPassword(password: String)

    fun removePassword()

    fun getPassword(): String?

    fun checkAppLock(): Boolean
}