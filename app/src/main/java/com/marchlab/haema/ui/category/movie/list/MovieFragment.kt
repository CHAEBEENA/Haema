package com.marchlab.haema.ui.category.movie.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentMovieBinding
import com.marchlab.haema.ui.category.movie.detail.MovieDetailActivity
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class MovieFragment : Fragment(R.layout.fragment_movie) {

    private val binding by viewBinding(FragmentMovieBinding::bind)

    private val viewModel: MovieViewModel by lifecycleScope.viewModel(this)

    private lateinit var mAdapter: MovieRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        mAdapter =
            MovieRecyclerViewAdapter(::onMovieClick)
        binding.movieRecyclerView.adapter = mAdapter
        binding.movieRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    private fun onMovieClick(id: Long) {
        startActivity(
            Intent(requireActivity(), MovieDetailActivity::class.java)
                .putExtra("movie_id", id)
        )
    }

    private fun setupObserver() {
        viewModel.movies.observe(viewLifecycleOwner) { result ->
            when(result) {
                Result.Loading -> { }
                is Result.Success -> {
                    if(result.data.isEmpty()) {
                    }
                    mAdapter.items =
                        result.data
                            .groupBy { it.date }
                            .flatMap { entry -> entry.value.sortedByDescending { it.id } }
                }
                is Result.Error -> { }
            }
            binding.movieEmptyLayout.isVisible = result is Result.Success && result.data.isEmpty()
        }
    }
}
