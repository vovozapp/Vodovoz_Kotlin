package com.vodovoz.app.feature.profile.notificationsettings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentNotificationSettingsBinding
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToBaseFormat
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToFullFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotificationSettingsFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_notification_settings

    private val binding: FragmentNotificationSettingsBinding by viewBinding {
        FragmentNotificationSettingsBinding.bind(contentView)
    }

    private val viewModel: NotificationSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
        observeEvents()
        initToolbar("Настройка уведомлений")
        binding.etPhone.setPhoneValidator {}
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeEvent()
                .collect {



                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUiState()
                .collect {
                    if (it.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    binding.phoneNubmerHeaderTv.text = it.data.item?.notSettingsData?.myPhone?.name ?: "Ваш номер мобильного телефона"
                    binding.infoTv.text = it.data.item?.notSettingsData?.title ?: "Для отказа от СМС-оповещений активируйте чекбокс и сохраните настройки."
                    binding.etPhone.setPhoneValidator {  }
                    binding.etPhone.setText(it.data.item?.notSettingsData?.myPhone?.active?.convertPhoneToBaseFormat()?.convertPhoneToFullFormat() ?: "")

                    showError(it.error)
                }
        }
    }



}