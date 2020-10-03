package com.marchlab.haema.ui.category.book.add

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.os.postDelayed
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentAddBookReviewBinding
import com.marchlab.haema.ui.category.CategoryDatePickerFragment
import com.marchlab.haema.util.extensions.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate

class AddBookReviewFragment : Fragment(R.layout.fragment_add_book_review) {

    private val binding by viewBinding(FragmentAddBookReviewBinding::bind)

    private val viewModel: AddBookViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupFragmentResultListener()

        setupObserver()
    }

    private fun init() {
        binding.bookReviewEditText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) binding.bookReviewEditText.showKeyboard()
            else binding.bookReviewEditText.hideKeyboard()
        }

        binding.bookReviewDateTextView.setOnClickListener {
            lifecycleScope.launch {
                binding.bookReviewEditText.clearFocus()

                parentFragmentManager.commitDelayed(250L) {
                    replace(R.id.add_book_root_layout, CategoryDatePickerFragment(), CategoryDatePickerFragment::class.java.simpleName)
                }
            }
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ -> viewModel.setRating(rating.toInt()) }

        binding.bookReviewEditText.doAfterTextChanged { review -> review?.let { viewModel.setReview(it.toString()) } }
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener(CategoryDatePickerFragment.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            bundle.getLong(CategoryDatePickerFragment.SELECTED_DATE)
                .takeIf { it > 0 }
                ?.let { viewModel.setDate(LocalDate.ofEpochDay(it)) }
        }
    }

    private fun setupObserver() {
        viewModel.page.observe(viewLifecycleOwner) { page ->
            if(page == 2) binding.bookReviewEditText.post { binding.bookReviewEditText.requestFocus() }
        }

        viewModel.rating.observe(viewLifecycleOwner) {
            if(binding.ratingBar.rating != it.toFloat()) binding.ratingBar.rating = it.toFloat()
        }

        viewModel.date.observe(viewLifecycleOwner) { binding.bookReviewDateTextView.text = it.toFormatString() }
    }
}