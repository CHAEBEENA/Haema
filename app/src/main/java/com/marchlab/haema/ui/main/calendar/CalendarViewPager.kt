package com.marchlab.haema.ui.main.calendar

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import java.util.*

class CalendarViewPager(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

    var onCalendarChangeListener: ((Calendar) -> Unit)? = null

    var onYearMonthChangeListener: ((Calendar) -> Unit)? = null
        set(value){
            field = value
            (adapter as? CalendarViewPagerAdapter)?.onYearMonthChangeListener = field
        }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        if(adapter is CalendarViewPagerAdapter) {
            this.clearOnPageChangeListeners()

            setCurrentItem(CalendarViewPagerAdapter.MAX_VALUE / 2, false)
            this.addOnPageChangeListener(pageChangeListener)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        /*val mode = MeasureSpec.getMode(heightMeasureSpec)
        if(mode == MeasureSpec.AT_MOST) {
            val view = focusedChild ?: getChildAt(0)
            view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val newHeight = view.measuredHeight

            val exactlyHeightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, exactlyHeightMeasureSpec)
        }*/
    }

    private val pageChangeListener = object : OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) { }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) { }

        override fun onPageSelected(position: Int) {
            val calendar = (adapter as? CalendarViewPagerAdapter)?.getCalendar(position) ?: return
            onCalendarChangeListener?.invoke(calendar)
        }
    }

    fun onYearMonthChanged(year: Int, month: Int) {
        val calendar = (adapter as? CalendarViewPagerAdapter)?.getCalendar(year, month) ?: return
        onCalendarChangeListener?.invoke(calendar)
        val current = Calendar.getInstance()
        val diff = (year - current.get(Calendar.YEAR)) * 12 + (month - current.get(Calendar.MONTH))
        setCurrentItem(CalendarViewPagerAdapter.MAX_VALUE / 2 + diff, false)
    }

}

