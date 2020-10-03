package com.marchlab.haema.ui.main.journal.emotion

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.marchlab.haema.R
import com.marchlab.haema.databinding.FragmentEmotionBinding
import com.marchlab.haema.domain.model.Emotion
import com.marchlab.haema.util.extensions.toast
import com.marchlab.haema.util.extensions.viewBinding
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import kotlin.properties.Delegates

/**
 * 감정 선택
 */
class EmotionFragment: Fragment(R.layout.fragment_emotion) {

    private val binding by viewBinding(FragmentEmotionBinding::bind)

    private val viewModel by lifecycleScope.viewModel<EmotionViewModel>(this)

    private lateinit var mAdapter: EmotionRecyclerViewAdapter

    private val emotions = Emotion.values().toList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupObserver()

        (arguments?.get(PREVIOUSLY_SELECTED_EMOTION_KEY) as? Emotion)
            ?.let { viewModel.onChange(it) }
    }

    private fun init() {
        binding.emotionRootLayout.setOnClickListener(::onSelection)
        binding.emotionSelectButton.setOnClickListener(::onSelection)

        mAdapter = EmotionRecyclerViewAdapter { viewModel.onChange(it) }
        binding.emotionRecyclerView.adapter = mAdapter
        binding.emotionRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        mAdapter.submitList(emotions)

        binding.emotionInnerLayout.setOnClickListener { /* block user input */ }
    }

    private fun setupObserver() {
        viewModel.emotion.observe(viewLifecycleOwner) { emotion ->
            mAdapter.selectedPosition = emotion.ordinal

            binding.emotionSelectButton.isSelected = true
            binding.emotionSelectButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_background))

            setFragmentResult(REQUEST_KEY, bundleOf(DISPLAY_EMOTION_KEY to emotion))
        }
    }

    private fun onSelection(view: View) {
        viewModel.emotion.value
            ?.let { setFragmentResult(REQUEST_KEY, bundleOf(SELECTED_EMOTION_KEY to it)) }
            ?: toast { "감정을 선택해주세요" }
    }

    companion object {
        const val REQUEST_KEY = "EMOTION_REQUEST_KEY"

        const val DISPLAY_EMOTION_KEY = "DISPLAY_EMOTION_KEY"

        const val SELECTED_EMOTION_KEY = "SELECTED_EMOTION_KEY"

        const val PREVIOUSLY_SELECTED_EMOTION_KEY = "PREVIOUS_SELECTED_EMOTION_KEY"
    }
}
