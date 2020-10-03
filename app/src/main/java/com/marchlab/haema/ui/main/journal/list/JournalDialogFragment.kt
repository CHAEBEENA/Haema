package com.marchlab.haema.ui.main.journal.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.marchlab.haema.R
import com.marchlab.haema.databinding.DialogJournalBinding
import com.marchlab.haema.util.extensions.viewBinding

/* naming */
class JournalDialogFragment: DialogFragment(R.layout.dialog_journal) {

    private val binding by viewBinding(DialogJournalBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rootLayout.setOnClickListener { dismiss() }

        binding.editTextView.setOnClickListener(::onEditClick)

        binding.deleteTextView.setOnClickListener(::onDeleteClick)

        binding.shareTextView.setOnClickListener(::onShareClick)
    }

    private fun onEditClick(view: View) {
        setFragmentResult(REQUEST_KEY, bundleOf(ACTION_KEY to EDIT))
        dismiss()
    }

    private fun onDeleteClick(view: View) {
        setFragmentResult(REQUEST_KEY, bundleOf(ACTION_KEY to DELETE))
        dismiss()
    }

    private fun onShareClick(view: View) {
        setFragmentResult(REQUEST_KEY, bundleOf(ACTION_KEY to SHARE))
        dismiss()
    }

    companion object {
        const val REQUEST_KEY = "JOURNAL_DIALOG_REQUEST_KEY"

        const val ACTION_KEY = "ACTION_KEY"

        const val EDIT = "EDIT"

        const val DELETE = "DELETE"

        const val SHARE = "SHARE"
    }
}