package com.marchlab.haema.ui.main.journal.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.marchlab.haema.R
import com.marchlab.haema.databinding.DialogAddJournalExitBinding
import com.marchlab.haema.util.extensions.viewBinding

class AddJournalExitDialogFragment: DialogFragment(R.layout.dialog_add_journal_exit) {

    private val binding by viewBinding(DialogAddJournalExitBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rootLayout.setOnClickListener{ requireActivity().onBackPressed() }

        binding.close.setOnClickListener { requireActivity().finish() }

        binding.remain.setOnClickListener { requireActivity().onBackPressed() }
    }
}