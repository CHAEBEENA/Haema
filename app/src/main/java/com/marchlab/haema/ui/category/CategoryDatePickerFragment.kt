package com.marchlab.haema.ui.category

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.marchlab.haema.R
import com.marchlab.haema.databinding.DialogCategoryDatePickerBinding
import com.marchlab.haema.util.extensions.viewBinding
import org.threeten.bp.LocalDate
import kotlin.properties.Delegates

class CategoryDatePickerFragment: DialogFragment(R.layout.dialog_category_date_picker) {

    private val binding by viewBinding(DialogCategoryDatePickerBinding::bind)

    private var mLocalDate by Delegates.observable(LocalDate.now()) { _, _, new ->
        binding.datePicker.maxValue = new.lengthOfMonth()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.yearPicker.apply {
            minValue = mLocalDate.year - 20
            maxValue = mLocalDate.year + 20
            value = mLocalDate.year

            setOnValueChangedListener { _, _, year -> mLocalDate = mLocalDate.withYear(year) }
        }

        binding.monthPicker.apply {
            minValue = 1
            maxValue = 12
            value = mLocalDate.monthValue

            setOnValueChangedListener { _, _, month -> mLocalDate = mLocalDate.withMonth(month) }
        }

        binding.datePicker.apply {
            minValue = 1
            maxValue = mLocalDate.lengthOfMonth()
            value = mLocalDate.dayOfMonth

            setOnValueChangedListener { _, _, dayOfMonth -> mLocalDate = mLocalDate.withDayOfMonth(dayOfMonth) }
        }

        binding.datePickerSelect.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(SELECTED_DATE to mLocalDate.toEpochDay()))
            (activity as? OnDateSelectListener)?.onDateSelect(mLocalDate)
            dismiss()
        }

        binding.datePickerClose.setOnClickListener { dismiss() }

        binding.datePickerRootLayout.setOnClickListener { dismiss() }

        binding.datePickerInnerLayout.setOnClickListener { /* nothing */ }
    }

    companion object {
        const val REQUEST_KEY = "CATEGORY_DATE_PICKER_REQUEST_KEY"

        const val SELECTED_DATE = "SELECTED_DATE"
    }

    interface OnDateSelectListener {
        fun onDateSelect(date: LocalDate)
    }
}
