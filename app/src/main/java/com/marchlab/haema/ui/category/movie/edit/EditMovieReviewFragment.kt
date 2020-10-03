package com.marchlab.haema.ui.category.movie.edit

import android.os.Bundle
import android.view.View
import androidx.core.view.postDelayed
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentEditMovieReviewBinding
import com.marchlab.haema.util.extensions.showKeyboard
import com.marchlab.haema.util.extensions.viewBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel

class EditMovieReviewFragment: Fragment(R.layout.fragment_edit_movie_review) {

    private val binding by viewBinding(FragmentEditMovieReviewBinding::bind)

    private val viewModel by lazy { requireParentFragment().getViewModel<EditMovieViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init(){
        binding.editMovieRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            viewModel.setRating(rating.toInt())
        }

        binding.editMovieReviewEditText.doAfterTextChanged { review ->
            review?.let { viewModel.setReview(it.toString()) }
        }

        binding.editMovieReviewEditText.postDelayed(250L) {
            binding.editMovieReviewEditText.requestFocus()
            binding.editMovieReviewEditText.setSelection(binding.editMovieReviewEditText.text.length) /* place cursor to the end */
            binding.editMovieReviewEditText.showKeyboard()
        }
    }

    private fun setupObserver() {

        viewModel.rating.observe(viewLifecycleOwner) { if(binding.editMovieRatingBar.rating != it.toFloat()) binding.editMovieRatingBar.rating = it.toFloat()}

        viewModel.review.observe(viewLifecycleOwner) { if(binding.editMovieReviewEditText.text.toString() != it) binding.editMovieReviewEditText.setText(it)}
    }

}