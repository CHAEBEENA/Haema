package com.marchlab.haema.util.extensions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver

val ViewGroup.layoutInflater: LayoutInflater get() = LayoutInflater.from(context)