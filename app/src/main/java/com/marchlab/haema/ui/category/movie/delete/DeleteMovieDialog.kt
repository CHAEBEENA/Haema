package com.marchlab.haema.ui.category.movie.delete

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.marchlab.haema.R
import com.marchlab.haema.databinding.DialogDeleteMovieBinding
import com.marchlab.haema.util.extensions.viewBinding

class DeleteMovieDialog: DialogFragment(R.layout.dialog_delete_movie) {

    private val binding by viewBinding(DialogDeleteMovieBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.deleteMovieRootLayout.setOnClickListener { dismiss() }

        binding.cancel.setOnClickListener { dismiss() }

        binding.delete.setOnClickListener { parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf()) }
    }

    companion object {
        const val REQUEST_KEY = "deleteMovieRequestKey"
    }
}