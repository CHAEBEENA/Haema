package com.marchlab.haema.ui.main.calendar

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.marchlab.haema.R
import com.marchlab.haema.util.extensions.supportFragmentManager
import kotlinx.android.synthetic.main.dialog_month_picker.*
import kotlinx.android.synthetic.main.dialog_month_picker.view.*


class YearMonthPickerDialog(
    private val year: Int,
    private val month: Int
): DialogFragment(R.layout.dialog_month_picker) {

    private var mYear: Int = 0

    private var mMonth: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_month_picker, container, false)

        mYear = year
        mMonth = month

        isCurrentMonth(view)

        val onMonthClickedListener = View.OnClickListener { v ->
            when(v.id){
                month_picker_january.id -> { changeMonth(0) }
                month_picker_february.id -> { changeMonth(1) }
                month_picker_march.id -> { changeMonth(2) }
                month_picker_april.id -> { changeMonth(3) }
                month_picker_may.id -> { changeMonth(4) }
                month_picker_june.id -> { changeMonth(5) }
                month_picker_july.id -> { changeMonth(6) }
                month_picker_august.id -> { changeMonth(7) }
                month_picker_september.id -> { changeMonth(8) }
                month_picker_october.id -> { changeMonth(9) }
                month_picker_november.id -> { changeMonth(10) }
                month_picker_december.id -> { changeMonth(11) }

            }
        }

        view.month_picker_root_layout.setOnClickListener {
            dismiss()
        }

        view.month_picker_year_text.text = resources.getString(R.string.calendar_year, mYear)

        view.month_picker_year_arrow_left.setOnClickListener {
            view.month_picker_year_text.text = resources.getString(R.string.calendar_year, --mYear)
            isCurrentMonth(view)
        }

        view.month_picker_year_arrow_right.setOnClickListener {
            view.month_picker_year_text.text = resources.getString(R.string.calendar_year, ++mYear)
            isCurrentMonth(view)
        }

        view.month_picker_january.setOnClickListener(onMonthClickedListener)
        view.month_picker_february.setOnClickListener(onMonthClickedListener)
        view.month_picker_march.setOnClickListener(onMonthClickedListener)
        view.month_picker_april.setOnClickListener(onMonthClickedListener)
        view.month_picker_may.setOnClickListener(onMonthClickedListener)
        view.month_picker_june.setOnClickListener(onMonthClickedListener)
        view.month_picker_july.setOnClickListener(onMonthClickedListener)
        view.month_picker_august.setOnClickListener(onMonthClickedListener)
        view.month_picker_september.setOnClickListener(onMonthClickedListener)
        view.month_picker_october.setOnClickListener(onMonthClickedListener)
        view.month_picker_november.setOnClickListener(onMonthClickedListener)
        view.month_picker_december.setOnClickListener(onMonthClickedListener)

        return view
    }

    private fun changeMonth(month: Int) {
        supportFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf("change_month" to arrayListOf(mYear, month)))
       // listener.onYearMonthChanged(mYear, month)
        dismiss()
    }

    private fun showMonthIndicator(layout: FrameLayout){
        val i = ImageView(context).apply {
            setImageResource(R.drawable.background_calendar_month_indicator)
            // set the ImageView bounds to match the Drawable's dimensions
            adjustViewBounds = true
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER
                setPadding(0,0,0,resources.getDimensionPixelOffset(R.dimen.margin_8))
            }

        }

        if(layout.childCount < 2){
            layout.addView(i)
            (layout.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_display))
        }
    }

    private fun removeMonthIndicator(layout: FrameLayout){
        if(layout.childCount == 2) {
            layout.removeViewAt(1)
            (layout.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
        }
    }

    private fun isCurrentMonth(view: View){
        if(mYear == year){
            when(month) {
                0->{ showMonthIndicator(view.month_picker_january) }
                1->{ showMonthIndicator(view.month_picker_february) }
                2->{ showMonthIndicator(view.month_picker_march) }
                3->{ showMonthIndicator(view.month_picker_april) }
                4->{ showMonthIndicator(view.month_picker_may) }
                5->{ showMonthIndicator(view.month_picker_june) }
                6->{ showMonthIndicator(view.month_picker_july) }
                7->{ showMonthIndicator(view.month_picker_august) }
                8->{ showMonthIndicator(view.month_picker_september) }
                9->{ showMonthIndicator(view.month_picker_october) }
                10->{ showMonthIndicator(view.month_picker_november) }
                11->{ showMonthIndicator(view.month_picker_december) }
            }
        } else {
            when(month) {
                0->{ removeMonthIndicator(view.month_picker_january) }
                1->{ removeMonthIndicator(view.month_picker_february) }
                2->{ removeMonthIndicator(view.month_picker_march) }
                3->{ removeMonthIndicator(view.month_picker_april) }
                4->{ removeMonthIndicator(view.month_picker_may) }
                5->{ removeMonthIndicator(view.month_picker_june) }
                6->{ removeMonthIndicator(view.month_picker_july) }
                7->{ removeMonthIndicator(view.month_picker_august) }
                8->{ removeMonthIndicator(view.month_picker_september) }
                9->{ removeMonthIndicator(view.month_picker_october) }
                10->{ removeMonthIndicator(view.month_picker_november) }
                11->{ removeMonthIndicator(view.month_picker_december) }
            }
        }
    }


    /** The system calls this only when creating the layout in a dialog. */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    companion object {
        const val REQUEST_KEY = "changeMonthRequestKey"
    }

}