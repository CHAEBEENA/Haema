package com.marchlab.haema.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.observe
import com.marchlab.haema.R
import com.marchlab.haema.databinding.ActivitySettingsBinding
import com.marchlab.haema.util.extensions.freeTrialPeriod
import com.marchlab.haema.util.extensions.toast
import com.marchlab.haema.util.extensions.viewBinding
import com.marchlab.haema.util.result.Result
import com.marchlab.haema.util.result.successOr
import com.marchlab.haema.util.result.successOrNull
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import timber.log.Timber
import timber.log.debug

class SettingsActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivitySettingsBinding::inflate)

    private val viewModel by lifecycleScope.viewModel<SettingsViewModel>(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(lifecycleScope)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()

        setupObserver()
    }

    private fun init() {
        binding.settingBackButton.setOnClickListener { finish() }

        /* 결제 */
        val left = freeTrialPeriod()
        binding.settingFreeTrialPeriodMsg.text = this.getString(R.string.free_trial_period, left)
        binding.settingDiscountMsg.text = this.getString(R.string.setting_discount_msg, left)

        binding.settingBilling.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.setting_root_layout, BillingFragment::class.java, null, BillingFragment::class.java.simpleName)
                .commit()
        }


        /* 비밀번호 */
        binding.lockScreenRefreshButton.setOnClickListener {
            startActivity(Intent(this, PasswordActivity::class.java))
        }

        binding.lockScreenSwitch.setOnClickListener {
            if(binding.lockScreenSwitch.isChecked) {
                startActivity(Intent(this, PasswordActivity::class.java))
            } else {
                viewModel.removePassword()
            }
        }

        /* 내보내기 */
        binding.settingExport.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.setting_root_layout, ExportFragment::class.java, null, ExportFragment::class.java.simpleName)
            }
        }

        /* 폰트 */
        binding.settingFont.setOnClickListener { toast(Toast.LENGTH_SHORT) { "폰트변경은 곧 업데이트 예정이에요" } }

        /* 백업 */
        binding.settingBackUp.setOnClickListener { toast(Toast.LENGTH_SHORT) { "백업기능은 곧 업데이트 예정이에요" } }

        /* 인스타그램 */
        binding.settingInstagram.setOnClickListener {
            val uriForApp: Uri = Uri.parse(getString(R.string.instagram_app_url))
            val instagramApp = Intent(Intent.ACTION_VIEW, uriForApp)

            val uriForBrowser: Uri = Uri.parse(getString(R.string.instgram_web_url))
            val instagramBrowser = Intent(Intent.ACTION_VIEW, uriForBrowser)

            try {
                startActivity(instagramApp)
            } catch (e: ActivityNotFoundException) {
                startActivity(instagramBrowser)
            }
        }
    }

    private fun setupObserver() {
        viewModel.isPurchased.observe(this) { result ->
            when(result) {
                Result.Loading -> {}
                else -> {
                    Timber.debug { "isPurchased; ${result.successOrNull()}" }

                    binding.settingBilling.isVisible = !result.successOr(false)

                    if(result.successOr(false)) binding.settingScreenLock.updateLayoutParams<ConstraintLayout.LayoutParams> { topMargin = 0 }
                }
            }
        }

        viewModel.isAppLock.observe(this) { result ->
            when(result) {
                Result.Loading -> {}
                is Result.Success -> {
                    binding.lockScreenSwitch.isChecked = result.data
                }
                is Result.Error -> {}
            }
        }

        supportFragmentManager.setFragmentResultListener(BillingFragment.REQUEST_KEY, this) { _, _ -> viewModel.checkIfPurchased() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAppLock()
    }
}
