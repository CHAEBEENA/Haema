package com.marchlab.haema.ui.category.movie.add

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentSearchMovieBinding
import com.marchlab.haema.domain.model.MovieSearchResult
import com.marchlab.haema.util.extensions.hideKeyboard
import com.marchlab.haema.util.extensions.showKeyboard
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchMovieFragment : Fragment(R.layout.fragment_search_movie) {

    private val viewModel : AddMovieViewModel by sharedViewModel()

    private val binding by viewBinding(FragmentSearchMovieBinding::bind)

    private lateinit var mAdapter: SearchMovieRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {

        binding.movieQueryEditText.postDelayed(250L) {
            binding.movieQueryEditText.requestFocus()
            binding.movieQueryEditText.showKeyboard()
        }

        binding.movieQueryEditText.doOnTextChanged { text, _, _, _ ->
            binding.movieClearEditText.isVisible = text?.isNotEmpty() == true
        }

        binding.movieQueryEditText.setOnEditorActionListener { v, _, _ ->
            viewModel.searchMovie(v.text.toString())
            binding.movieQueryEditText.clearFocus()
            binding.movieQueryEditText.hideKeyboard()
            true
        }

        binding.movieSearchIcon.setOnClickListener {
            binding.movieQueryEditText.text
                .takeIf { it.isNotBlank() }
                ?.let { viewModel.searchMovie(it.toString()) }

            binding.movieQueryEditText.clearFocus()

            binding.movieQueryEditText.hideKeyboard()
        }

        mAdapter = SearchMovieRecyclerViewAdapter(::onMovieResultItemClick)
        binding.movieSearchRecyclerView.adapter = mAdapter
        binding.movieSearchRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.movieClearEditText.setOnClickListener { binding.movieQueryEditText.text.clear() }

        binding.errorLayout.setOnClickListener {  }

        binding.retry.setOnClickListener { viewModel.searchMovie(binding.movieQueryEditText.text.toString()) }

        binding.movieSearchAddManually.setOnClickListener(::addManually)
    }

    private fun setupObserver() {
        viewModel.searchMovieResult.observe(viewLifecycleOwner, Observer { result ->
            when(result) {
                Result.Loading -> { }
                is Result.Success -> {
                    mAdapter.items = result.data
                }
                is Result.Error -> { }
            }
            binding.movieSearchLoadingLayout.isVisible = result is Result.Loading
            binding.movieSearchEmptyStateLayout.isVisible = result is Result.Success && result.data.isEmpty()
            binding.errorLayout.isVisible = result is Result.Error
        })
    }

    private fun onMovieResultItemClick(movie: MovieSearchResult) {
        viewModel.onMovieClick(movie)
        viewModel.onPageChange(1)
    }

    private fun addManually(view: View) = viewModel.onPageChange(1)



}
