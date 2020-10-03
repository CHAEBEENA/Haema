package com.marchlab.haema.ui.main.calendar

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marchlab.haema.databinding.ItemCalendarBinding
import com.marchlab.haema.domain.model.Emotion
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.util.extensions.layoutInflater
import org.apache.commons.lang3.time.DateUtils
import org.threeten.bp.LocalDate
import java.util.*
import kotlin.properties.Delegates

class DayGridAdapter(
    private val context: Context,
    private val calendar: Calendar,
    startingAt: CalendarViewPagerAdapter.DayOfWeek,
    private val journals: List<Journal>,
    private val onClickAction: (date: LocalDate, isJournal: Boolean) -> Unit
) : RecyclerView.Adapter<DayViewHolder>() {

    private val weekOfMonth: Int
    private val startDate: Calendar

    var items: List<Day> by Delegates.observable(emptyList()) { _, old, new ->
        CalendarDiff(old, new).calculateDiff().dispatchUpdatesTo(this)
    }


    init {
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
        startDate = start
        this.weekOfMonth = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) + (if (startingAt.isLessFirstWeek(calendar)) 1 else 0) - (if (startingAt.isMoreLastWeek(calendar)) 1 else 0)
        //Log.d("coco-dev",weekOfMonth.toString())
        updateItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder = DayViewHolder(ItemCalendarBinding.inflate(parent.layoutInflater, parent, false), onClickAction)

    override fun getItemCount(): Int = 7 * 6


    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(items[holder.layoutPosition])
    }

    private fun updateItems() {
        val now = Calendar.getInstance()

        this.items = (0..itemCount).map {
            val cal = Calendar.getInstance().apply { time = startDate.time }
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

            var emotion: Emotion? = null

            journals.forEach {it->
                val year = it.date.year
                val month = it.date.monthValue-1
                val day = it.date.dayOfMonth
                if(year == cal.get(Calendar.YEAR) && month == cal.get(Calendar.MONTH) && day == cal.get(Calendar.DAY_OF_MONTH)){
                    emotion = it.emotion
                }
            }

            Day(
                cal,
                state,
                isToday,
                isFuture,
                emotion
            )
        }
    }




}