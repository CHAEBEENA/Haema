package com.marchlab.haema.data.preference

import android.content.Context

class HaemaPreferenceStorage(context: Context): PreferenceStorage {

    private val preferences by lazy { context.getSharedPreferences("com.marchlab.haema.data.preference", Context.MODE_PRIVATE) }

    override var password by StringPreference(preferences, PASSWORD, "")

    override var appLock by BooleanPreference(preferences, IS_LOCK, false)

    override var purchased by BooleanPreference(preferences, PURCHASED, false)

    /* override var isAcknowledged by BooleanPreference(preferences, IS_ACKNOWLEDGED, false) */

    companion object {
        private const val PASSWORD = "PASSWORD"
        private const val IS_LOCK = "IS_LOCK"
        private const val PURCHASED = "PURCHASED"
        private const val IS_ACKNOWLEDGED = "IS_ACKNOWLEDGED"
    }
}