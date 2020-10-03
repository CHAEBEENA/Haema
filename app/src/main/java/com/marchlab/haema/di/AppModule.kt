package com.marchlab.haema.di

import com.marchlab.haema.ui.ApplicationObserver
import com.marchlab.haema.ui.settings.BillingFragment
import com.marchlab.haema.ui.settings.BillingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val appModule = module {

    single { ApplicationObserver(androidContext(), get()) }

    fragment { BillingFragment() }

    scope<BillingFragment> {
        viewModel { BillingViewModel(get(), get()) }
    }
}