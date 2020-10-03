package com.marchlab.haema

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.jakewharton.threetenabp.AndroidThreeTen
import com.marchlab.haema.di.*
import com.marchlab.haema.ui.ApplicationObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import timber.log.LogcatTree
import timber.log.Timber

@ExperimentalCoroutinesApi class HaemaApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        startKoin {
            androidContext(this@HaemaApplication)

            androidLogger()

            androidFileProperties()

            fragmentFactory()

            modules(appModule + databaseModule + networkModule + dataSourceModule + domainModule + uiModule)
        }


        ProcessLifecycleOwner.get().lifecycle.addObserver(get<ApplicationObserver>())

        Timber.plant(LogcatTree("haema-application"))
    }
}