package com.marchlab.haema.ui.category.book.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentSearchBookBinding
import com.marchlab.haema.domain.model.BookSearchResult
import com.marchlab.haema.util.extensions.hideKeyboard
import com.marchlab.haema.util.extensions.showKeyboard
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import com.marchlab.haema.util.result.successOrNull
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddBookSearchFragment: Fragment(R.layout.fragment_search_book) {

    private val binding by viewBinding(FragmentSearchBookBinding::bind)

    private val viewModel: AddBookViewModel by sharedViewModel()

    private lateinit var mAdapter: AddBookSearchRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()
    }

    private fun init() {
        binding.bookQueryEditText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) binding.bookQueryEditText.showKeyboard()
            else binding.bookQueryEditText.hideKeyboard()
        }

        binding.bookQueryEditText.postDelayed(250L) {
            binding.bookQueryEditText.requestFocus()
        }

        binding.bookQueryEditText.doAfterTextChanged { query -> binding.bookClearEditText.isVisible = !query.isNullOrEmpty() }

        binding.bookQueryEditText.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.bookQueryEditText.text
                    .takeIf { it.isNotBlank() }
                    ?.let { viewModel.search(it.toString()) }

                binding.bookQueryEditText.clearFocus()
            }

            actionId == EditorInfo.IME_ACTION_SEARCH
        }

        binding.bookClearEditText.setOnClickListener {
            binding.bookQueryEditText.text.clear()
            binding.bookQueryEditText.requestFocus()
        }

        mAdapter = AddBookSearchRecyclerViewAdapter(::onBookItemClick)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.addManually.setOnClickListener { viewModel.onPageChange(AddBookActivity.PAGE_BOOK_INFO) }

        binding.retry.setOnClickListener { viewModel.search(binding.bookQueryEditText.text.toString()) }

        binding.bookSearchIcon.setOnClickListener {
            binding.bookQueryEditText.text
                .takeIf { it.isNotBlank() }
                ?.let { viewModel.search(it.toString()) }

            binding.bookQueryEditText.clearFocus()
        }

        binding.searchBookLoading.setOnClickListener { /* block user input */ }
    }

    private fun onBookItemClick(book: BookSearchResult) {
        viewModel.onBookSelected(book)
        viewModel.onPageChange(AddBookActivity.PAGE_BOOK_INFO)
    }

    private fun setupObserver() {
        viewModel.searchBookResult.observe(viewLifecycleOwner) { result ->
            binding.searchBookLoading.isVisible = result is Result.Loading

            result.successOrNull()
                ?.let { books ->
                    binding.emptyStateLayout.isVisible = books.isEmpty()
                    mAdapter.submitList(books)
                }

            binding.errorLayout.isVisible = result is Result.Error
        }
    }
}