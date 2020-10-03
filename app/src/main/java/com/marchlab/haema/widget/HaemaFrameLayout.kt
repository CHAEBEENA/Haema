package com.marchlab.haema.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.marchlab.haema.databinding.WidgetHaemaFrameLayoutBinding
import com.marchlab.haema.util.extensions.layoutInflater

class HaemaFrameLayout: FrameLayout {

    private var mBinding: WidgetHaemaFrameLayoutBinding? = null

    constructor(context: Context): super(context) { init(context) }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) { init(context) }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(context) }

    private fun init(context: Context) {
        mBinding = WidgetHaemaFrameLayoutBinding.inflate(layoutInflater, this, true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mBinding = null
    }
}