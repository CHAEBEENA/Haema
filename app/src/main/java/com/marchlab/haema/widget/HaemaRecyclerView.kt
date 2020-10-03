package com.marchlab.haema.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import timber.log.debug

class HaemaRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var mY = 0.0f

    private var mPercent = 0.0f

    private var onIntercept = false

    var guidePercent = 0.5f

    var onActionMove: ((percent: Float) -> Unit)? = null

    var onActionUp: ((percent: Float) -> Unit)? = null

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                mY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                mPercent = (event.rawY - mY) / resources.displayMetrics.heightPixels

                mY = event.rawY

                if(onIntercept) {
                    onActionMove?.apply {

                        invoke(mPercent)

                        return true
                    }
                }

                if(!canScrollVertically(1) && mPercent < 0 && guidePercent == 0.5f) {
                    onActionMove?.apply {

                        onIntercept = true

                        invoke(mPercent)

                        return true
                    }
                }

                if(!canScrollVertically(-1) && (mPercent > 0.035 && guidePercent == 0.0f) || (mPercent < -0.035 && guidePercent == 0.5f)) {
                    onActionMove?.apply {

                        onIntercept = true

                        invoke(mPercent)

                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if(onIntercept) {
                    onActionUp?.apply {

                        onIntercept = false

                        mY = 0.0f

                        invoke(mPercent)

                        return true
                    }
                }

                mY = 0.0f
            }
        }

        return super.dispatchTouchEvent(event)
    }
}