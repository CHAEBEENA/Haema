package com.marchlab.haema.ui.category.book.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.postDelayed
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentEditBookReviewBinding
import com.marchlab.haema.ui.category.CategoryDatePickerFragment
import com.marchlab.haema.util.extensions.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.koin.getViewModel

@ExperimentalCoroutinesApi
class EditBookReviewFragment: Fragment(R.layout.fragment_edit_book_review) {

    private val binding by viewBinding(FragmentEditBookReviewBinding::bind)

    private val viewModel by sharedViewModel<EditBookViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        binding.editBookRatingBar.setOnRatingBarChangeListener { _, rating, _ -> viewModel.setRating(rating.toInt()) }

        binding.editBookDateTextView.setOnClickListener {
            lifecycle.coroutineScope.launch {
                binding.editBookReviewEditText.clearFocus()
                binding.editBookReviewEditText.hideKeyboard()

                delay(250L)

                parentFragmentManager.commit {
                    replace(R.id.edit_book_root_layout, CategoryDatePickerFragment::class.java, null, CategoryDatePickerFragment::class.java.name)
                }
            }
        }

        binding.editBookReviewEditText.doAfterTextChanged { review ->
            review?.let { viewModel.setReview(it.toString()) }
        }

        binding.editBookReviewEditText.postDelayed(250L) {
            binding.editBookReviewEditText.requestFocus()
            binding.editBookReviewEditText.setSelection(binding.editBookReviewEditText.text.length)
            binding.editBookReviewEditText.showKeyboard()
        }
    }

    private fun setupObserver() {
        viewModel.rating.observe(viewLifecycleOwner) { rating ->
            if(binding.editBookRatingBar.rating != rating.toFloat()) binding.editBookRatingBar.rating = rating.toFloat()
        }

        viewModel.date.observe(viewLifecycleOwner) { date ->
            binding.editBookDateTextView.text = date.toFormatString()
        }

        viewModel.review.observe(viewLifecycleOwner) { review ->
            if(binding.editBookReviewEditText.text.toString() != review) binding.editBookReviewEditText.setText(review)
        }
    }
}
