package com.marchlab.haema.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.marchlab.haema.data.preference.HaemaPreferenceStorage
import com.marchlab.haema.ui.applock.AppLockActivity

open class ApplicationObserver(private val context: Context, private val prefs: HaemaPreferenceStorage) : LifecycleObserver {

    init {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        Log.d("coco-dev","APPLICATION ON_START")
        if(prefs.appLock) {
            context.startActivity(Intent(context, AppLockActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK))
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
        Log.d("coco-dev","APPLICATION ON_RESUME")

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        Log.d("coco-dev","APPLICATION ON_STOP")
    }
}