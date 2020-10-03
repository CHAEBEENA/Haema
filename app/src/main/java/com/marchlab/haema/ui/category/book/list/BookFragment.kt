package com.marchlab.haema.ui.category.book.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentBookBinding
import com.marchlab.haema.ui.category.book.detail.BookDetailActivity
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import com.marchlab.haema.util.result.successOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

@ExperimentalCoroutinesApi
class BookFragment : Fragment(R.layout.fragment_book) {

    private val binding by viewBinding(FragmentBookBinding::bind)

    private val viewModel: BookViewModel by lifecycleScope.viewModel(this)

    private lateinit var mAdapter: BookRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        mAdapter = BookRecyclerViewAdapter(::onBookItemClicked)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    private fun onBookItemClicked(id: Long) = BookDetailActivity.startActivity(requireContext(), id)

    private fun setupObserver() {
        viewModel.books.observe(viewLifecycleOwner, Observer { result ->
            result.successOrNull()
                ?.let { books ->
                    binding.bookEmptyLayout.isVisible = books.isEmpty()

                    books
                        .groupBy { it.date }
                        .flatMap { entry -> entry.value.sortedByDescending { it.id } }
                        .let { mAdapter.submitList(it) }
                }
        })
    }
}
