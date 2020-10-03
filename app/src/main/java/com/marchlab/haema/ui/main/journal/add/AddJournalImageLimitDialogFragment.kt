package com.marchlab.haema.ui.main.journal.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.marchlab.haema.R
import com.marchlab.haema.databinding.DialogImageLimitBinding
import com.marchlab.haema.util.extensions.viewBinding

class AddJournalImageLimitDialogFragment: Fragment(R.layout.dialog_image_limit) {

    private val binding by viewBinding(DialogImageLimitBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rootLayout.setOnClickListener { requireActivity().onBackPressed() }

        binding.close.setOnClickListener { requireActivity().onBackPressed() }
    }
}