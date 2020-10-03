package com.marchlab.haema.ui.category.book.delete

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.marchlab.haema.R
import com.marchlab.haema.databinding.DialogDeleteBookBinding
import com.marchlab.haema.util.extensions.viewBinding

class DeleteBookDialogFragment: DialogFragment(R.layout.dialog_delete_book) {

    private val binding by viewBinding(DialogDeleteBookBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.deleteBookRootLayout.setOnClickListener { dismiss() }

        binding.cancel.setOnClickListener { dismiss() }

        binding.delete.setOnClickListener { parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf()) }
    }

    companion object {
        const val REQUEST_KEY = "DELETE_BOOK_DIALOG_REQUEST_KEY"
    }
}
