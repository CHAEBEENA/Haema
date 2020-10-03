package com.marchlab.haema.ui.applock

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityPasswordBinding
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import com.marchlab.haema.util.result.successOr
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class AppLockActivity: AppCompatActivity() {

    companion object{
        const val passwordCount = 4
    }

    private val binding by viewBinding(ActivityPasswordBinding::inflate)

    private val viewModel: AppLockViewModel by lifecycleScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()

        setupObserver()
    }

    private fun init() {
        binding.passwordMsgText.setText(R.string.password_msg)

        binding.text1.setOnClickListener { appendInputText(binding.text1.text.toString()) }
        binding.text2.setOnClickListener { appendInputText(binding.text2.text.toString()) }
        binding.text3.setOnClickListener { appendInputText(binding.text3.text.toString()) }
        binding.text4.setOnClickListener { appendInputText(binding.text4.text.toString()) }
        binding.text5.setOnClickListener { appendInputText(binding.text5.text.toString()) }
        binding.text6.setOnClickListener { appendInputText(binding.text6.text.toString()) }
        binding.text7.setOnClickListener { appendInputText(binding.text7.text.toString()) }
        binding.text8.setOnClickListener { appendInputText(binding.text8.text.toString()) }
        binding.text9.setOnClickListener { appendInputText(binding.text9.text.toString()) }
        binding.text0.setOnClickListener { appendInputText(binding.text0.text.toString()) }
        binding.delete.setOnClickListener { removeInputText() }
    }

    private fun appendInputText(text: String) {
        viewModel.appendInput(text)
    }

    private fun removeInputText() {
        viewModel.removeInput()
    }

    private fun setupObserver(){
        viewModel.password.observe(this) {result ->
            when(result) {
                Result.Loading -> {}
                is Result.Success -> {

                }
                is Result.Error -> {}
            }
        }

        viewModel.input.observe(this) {
            Log.d("coco-dev","password : ${viewModel.password.value?.successOr("")}")
            when(it.size) {
                0 -> {
                    binding.input1.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                    binding.input2.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                    binding.input3.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                    binding.input4.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                }
                1 -> {
                    binding.input1.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)
                    binding.input2.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                    binding.input3.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                    binding.input4.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                }
                2-> {
                    binding.input1.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)
                    binding.input2.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)
                    binding.input3.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                    binding.input4.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                }
                3-> {
                    binding.input1.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)
                    binding.input2.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)
                    binding.input3.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)
                    binding.input4.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_line)
                }
                4-> {
                    binding.input1.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)
                    binding.input2.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)
                    binding.input3.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)
                    binding.input4.background = ContextCompat.getDrawable(this@AppLockActivity, R.drawable.background_password_input_circle)

                    if (viewModel.password.value?.successOr("") == it.joinToString("")) {
                        finish()
                    } else {
                        binding.passwordMsgText.setText(R.string.password_wrong_msg)
                        viewModel.clearInput()
                    }

                }
            }
        }
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}