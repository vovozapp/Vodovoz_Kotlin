package com.vodovoz.app.feature.profile.biometric

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup.OnCheckedChangeListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentBiometricSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BiometricSettingsFragment : BaseFragment() {

    @Inject
    lateinit var accountManager: AccountManager

    override fun layout(): Int = R.layout.fragment_biometric_settings

    private val binding: FragmentBiometricSettingsBinding by viewBinding {
        FragmentBiometricSettingsBinding.bind(contentView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.personalDataSwitch.isChecked = accountManager.fetchUseBio()

        initToolbar("Безопасность")

        binding.personalDataSwitch.setOnCheckedChangeListener { p0, p1 -> accountManager.saveUseBio(p1) }

    }
}