package com.marchlab.haema.util.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.postDelayed

val View.layoutInflater: LayoutInflater get() = LayoutInflater.from(context)