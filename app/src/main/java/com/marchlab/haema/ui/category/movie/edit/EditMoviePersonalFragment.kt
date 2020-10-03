package com.marchlab.haema.ui.category.movie.edit

import android.os.Bundle
import android.view.View
import androidx.core.view.postDelayed
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentEditMoviePersonalBinding
import com.marchlab.haema.ui.category.CategoryDatePickerFragment
import com.marchlab.haema.util.extensions.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class EditMoviePersonalFragment :Fragment(R.layout.fragment_edit_movie_personal){

    private val binding by viewBinding(FragmentEditMoviePersonalBinding::bind)

    private val viewModel by lazy { requireParentFragment().getViewModel<EditMovieViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {

        binding.editMovieReviewDateTextView.setOnClickListener {
            binding.editMoviePlaceEditText.clearFocus()
            binding.editMoviePlaceEditText.hideKeyboard()

            binding.editMoviePeopleEditText.clearFocus()
            binding.editMoviePeopleEditText.hideKeyboard()

            lifecycle.coroutineScope.launch {
                delay(250L)

                supportFragmentManager.beginTransaction()
                    .add(R.id.movie_detail_root_layout, CategoryDatePickerFragment::class.java, null, CategoryDatePickerFragment::class.java.simpleName)
                    .commit()
            }
        }

        binding.editMoviePlaceEditText.doAfterTextChanged { place ->
            place?.let { viewModel.setPlace(it.toString()) }
        }

        binding.editMoviePeopleEditText.doAfterTextChanged { people ->
            people?.let { viewModel.setPeople(it.toString()) }
        }

        binding.editMoviePlaceEditText.postDelayed(250L) {
            binding.editMoviePlaceEditText.requestFocus()
            binding.editMoviePlaceEditText.setSelection(binding.editMoviePlaceEditText.text.length) /* place cursor to the end */
            binding.editMoviePlaceEditText.showKeyboard()
        }

    }

    private fun setupObserver() {

        viewModel.date.observe(viewLifecycleOwner) { binding.editMovieReviewDateTextView.text = it.toFormatString() }

        viewModel.place.observe(viewLifecycleOwner) { if(binding.editMoviePlaceEditText.text.toString() != it) binding.editMoviePlaceEditText.setText(it) }

        viewModel.people.observe(viewLifecycleOwner) { if(binding.editMoviePeopleEditText.text.toString() != it) binding.editMoviePeopleEditText.setText(it) }
    }


}