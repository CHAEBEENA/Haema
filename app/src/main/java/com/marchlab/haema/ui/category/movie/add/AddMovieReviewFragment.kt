package com.marchlab.haema.ui.category.movie.add

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentAddMovieReviewBinding
import com.marchlab.haema.util.extensions.showKeyboard
import com.marchlab.haema.util.extensions.viewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddMovieReviewFragment : Fragment(R.layout.fragment_add_movie_review) {

    private val viewModel: AddMovieViewModel by sharedViewModel()

    private val binding by viewBinding(FragmentAddMovieReviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init(){
        binding.addMovieRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            viewModel.setRating(rating.toInt())
        }

        binding.addMovieReviewEditText.doAfterTextChanged { review ->
            review?.let { viewModel.setReview(it.toString()) }
        }
    }

    private fun setupObserver() {
        viewModel.page.observe(viewLifecycleOwner) { page ->
            if(page == 3) {
                binding.addMovieReviewEditText.requestFocus()
                binding.addMovieReviewEditText.showKeyboard()
            }
        }
    }

}
