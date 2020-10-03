package com.marchlab.haema.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivityPasswordBinding
import com.marchlab.haema.ui.settings.PasswordViewModel
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class PasswordActivity : AppCompatActivity() {

    private var newPassword : String? = null

    private val binding by viewBinding(ActivityPasswordBinding::inflate)

    private val viewModel: PasswordViewModel by lifecycleScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
        setupObserver()
    }

    private fun init() {
        binding.passwordMsgText.text = getString(R.string.password_new_msg)

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

    private fun setupObserver() {
        viewModel.input.observe(this) {
            when (it.size) {
                0 -> {
                    binding.input1.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                    binding.input2.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                    binding.input3.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                    binding.input4.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                }
                1 -> {
                    binding.input1.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)
                    binding.input2.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                    binding.input3.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                    binding.input4.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                }
                2 -> {
                    binding.input1.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)
                    binding.input2.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)
                    binding.input3.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                    binding.input4.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                }
                3 -> {
                    binding.input1.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)
                    binding.input2.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)
                    binding.input3.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)
                    binding.input4.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_line)
                }
                4 -> {
                    binding.input1.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)
                    binding.input2.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)
                    binding.input3.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)
                    binding.input4.background = ContextCompat.getDrawable(this, R.drawable.background_password_input_circle)


                    if(newPassword.isNullOrBlank()) {
                        binding.passwordMsgText.text = getString(R.string.password_confirm_msg)
                        newPassword = it.joinToString("")
                        viewModel.clearInput()
                    } else {
                        if (newPassword == it.joinToString("")) {
                            viewModel.setPassword(it.joinToString(""))
                        } else {
                            binding.passwordMsgText.text = getString(R.string.password_wrong_msg)
                            viewModel.clearInput()
                        }
                    }
                }
            }
        }

        viewModel.setPasswordResult.observe(this) {result->
            when(result) {
                Result.Loading -> { }
                is Result.Success -> {
                    finish()
                }
                is Result.Error -> { }
            }
        }
    }
}
