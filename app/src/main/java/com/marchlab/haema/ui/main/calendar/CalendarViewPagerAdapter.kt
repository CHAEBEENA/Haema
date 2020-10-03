package com.marchlab.haema.ui.main.calendar

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.marchlab.haema.domain.model.Journal
import org.apache.commons.lang3.time.DateUtils
import org.threeten.bp.LocalDate
import java.util.*

open class CalendarViewPagerAdapter(
    val context: Context,
    private val onClickAction: (date: LocalDate, isJournal: Boolean) -> Unit,
    base: Calendar = Calendar.getInstance(),
    private val startingAt: DayOfWeek = DayOfWeek.Sunday
) : PagerAdapter() {

    companion object {
        const val MAX_VALUE = 500
    }

    var journals = listOf<Journal>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val baseCalendar: Calendar = DateUtils.truncate(base, Calendar.DAY_OF_MONTH).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        firstDayOfWeek = Calendar.SUNDAY + startingAt.getDifference()
        minimalDaysInFirstWeek = 1
    }

    private var viewContainer: ViewGroup? = null

    var onYearMonthChangeListener: ((Calendar) -> Unit)? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object`)

    override fun getCount(): Int = MAX_VALUE

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        /* val now = Calendar.getInstance()
         val calendar = getCalendar(position)

         val start = DateUtils.truncate(calendar, Calendar.DAY_OF_MONTH)
         if (start.get(Calendar.DAY_OF_WEEK) != (startingAt.getDifference() + 1)) {
             start.set(
                 Calendar.DAY_OF_MONTH,
                 if (startingAt.isLessFirstWeek(calendar)) -startingAt.getDifference() else 0
             )
             start.add(
                 Calendar.DAY_OF_MONTH,
                 -start.get(Calendar.DAY_OF_WEEK) + 1 + startingAt.getDifference()
             )
         }

         val recyclerViewAdapter = DayGridAdapter(context, getCalendar(position), startingAt, emotions)
         recyclerViewAdapter.items = (0..recyclerViewAdapter.itemCount).map {
             val cal = Calendar.getInstance().apply { time = start.time }
             cal.add(Calendar.DAY_OF_MONTH, it)

             val thisTime = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH)
             val compareTime = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH)

             val state = when (thisTime.compareTo(compareTime)) {
                 -1 -> DayState.NextMonth
                 0 -> DayState.ThisMonth
                 1 -> DayState.PreviousMonth
                 else -> throw IllegalStateException()
             }
             val isToday = DateUtils.isSameDay(cal, now)

             val isFuture = cal > now

             var emotion: EmotionSample? = null
             Log.e("coco-dev","onBind")
             emotions.forEach {it->
                 val c = Calendar.getInstance()
                 c.timeInMillis = it.time
                 val year = c.get(Calendar.YEAR)
                 val month = c.get(Calendar.MONTH)
                 val day = c.get(Calendar.DAY_OF_MONTH)
                 Log.d("coco-dev","$year $month $day ${cal.get(Calendar.MONTH)} ${cal.get(Calendar.DAY_OF_MONTH)}")
                 if(year == cal.get(Calendar.YEAR) && month == cal.get(Calendar.MONTH) && day == cal.get(Calendar.DAY_OF_MONTH)){
                     emotion = it
                 }
             }

             Day(
                 cal,
                 state,
                 isToday,
                 isFuture,
                 emotion
             )
         }*/
        val recyclerView = RecyclerView(context).apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(context, 7)
            isNestedScrollingEnabled = false
            hasFixedSize()
            adapter = DayGridAdapter(context, getCalendar(position), startingAt, journals, onClickAction)
        }
        container.addView(recyclerView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        viewContainer = container

        return recyclerView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun getCalendar(position: Int): Calendar {
        return (baseCalendar.clone() as Calendar).apply {
            add(Calendar.MONTH, position - MAX_VALUE / 2)
        }
    }

    fun getCalendar(year: Int, month: Int): Calendar {
        return (baseCalendar.clone() as Calendar).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }
    }

    enum class DayOfWeek {
        Sunday,
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday;

        //기준(일요일)에서 차이
        fun getDifference(): Int {
            return when (this) {
                Sunday -> 0
                Monday -> 1
                Tuesday -> 2
                Wednesday -> 3
                Thursday -> 4
                Friday -> 5
                Saturday -> 6
            }
        }

        fun isLessFirstWeek(calendar: Calendar): Boolean {
            return calendar.get(Calendar.DAY_OF_WEEK) < getDifference() + 1
        }

        fun isMoreLastWeek(calendar: Calendar): Boolean {
            val end = DateUtils.truncate(calendar, Calendar.DAY_OF_MONTH)
            end.add(Calendar.MONTH, 1)
            end.add(Calendar.DATE, -1)
            return end.get(Calendar.DAY_OF_WEEK) < getDifference() + 1
        }

    }



}