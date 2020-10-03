package com.marchlab.haema.util.extensions

import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.smoothScrollTo(position: Int) {

    val smoothScroller = object: LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int = SNAP_TO_START
    }.apply { targetPosition = position }

    layoutManager?.startSmoothScroll(smoothScroller)
}