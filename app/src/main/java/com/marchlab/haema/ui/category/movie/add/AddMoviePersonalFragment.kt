package com.marchlab.haema.ui.category.movie.add

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.os.postDelayed
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentAddMoviePersonalBinding
import com.marchlab.haema.ui.category.CategoryDatePickerFragment
import com.marchlab.haema.util.extensions.hideKeyboard
import com.marchlab.haema.util.extensions.showKeyboard
import com.marchlab.haema.util.extensions.toFormatString
import com.marchlab.haema.util.extensions.viewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate

class AddMoviePersonalFragment : Fragment(R.layout.fragment_add_movie_personal) {

    private val viewModel: AddMovieViewModel by sharedViewModel()

    private val binding by viewBinding(FragmentAddMoviePersonalBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupFragmentResultListener()

        setupObserver()
    }

    private fun init() {

        binding.addMovieReviewDateTextView.setOnClickListener {
            binding.addMoviePlaceEditText.clearFocus()
            binding.addMoviePlaceEditText.hideKeyboard()

            binding.addMoviePeopleEditText.clearFocus()
            binding.addMoviePeopleEditText.hideKeyboard()

            Handler().postDelayed(250L) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.add_movie_root_layout, CategoryDatePickerFragment(), CategoryDatePickerFragment::class.java.simpleName)
                    .commit()
            }
        }

        binding.addMoviePlaceEditText.doAfterTextChanged { place ->
            place?.let { viewModel.setPlace(it.toString()) }
        }

        binding.addMoviePeopleEditText.doAfterTextChanged { people ->
            people?.let { viewModel.setPeople(it.toString()) }
        }
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener(CategoryDatePickerFragment.REQUEST_KEY, viewLifecycleOwner) {_, bundle ->
            val epochDay = bundle.getLong(CategoryDatePickerFragment.SELECTED_DATE)
            if (epochDay > 0) viewModel.setDate(LocalDate.ofEpochDay(epochDay))
        }
    }

    private fun setupObserver() {
        viewModel.page.observe(viewLifecycleOwner) { page ->
            if(page == 2) {
                binding.addMoviePlaceEditText.post {
                    binding.addMoviePlaceEditText.requestFocus()
                    binding.addMoviePlaceEditText.setSelection(binding.addMoviePlaceEditText.text.length)
                    binding.addMoviePlaceEditText.showKeyboard()
                }
            }
        }

        viewModel.date.observe(viewLifecycleOwner) { binding.addMovieReviewDateTextView.text = it.toFormatString() }
    }
}
