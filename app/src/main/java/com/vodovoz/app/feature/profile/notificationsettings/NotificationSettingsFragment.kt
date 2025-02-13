package com.vodovoz.app.feature.profile.notificationsettings

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentNotificationSettingsBinding
import com.vodovoz.app.feature.profile.notificationsettings.adapter.NotSettingsClickListener
import com.vodovoz.app.feature.profile.notificationsettings.adapter.NotSettingsController
import com.vodovoz.app.feature.profile.notificationsettings.model.NotSettingsItem
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToBaseFormat
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToFullFormat
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationSettingsFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_notification_settings

    private val binding: FragmentNotificationSettingsBinding by viewBinding {
        FragmentNotificationSettingsBinding.bind(contentView)
    }

    internal val viewModel: NotificationSettingsViewModel by viewModels()

    private val notSettingsController: NotSettingsController by lazy {
        NotSettingsController(object: NotSettingsClickListener {
            override fun onItemChecked(item: NotSettingsItem) {
                viewModel.saveItem(item)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
        observeEvents()
        initToolbar("Настройка уведомлений")
        notSettingsController.bind(binding.notificationSettingsRv)

        //binding.saveBtn.setOnClickListener {
        //    if (!validatePhone(binding.phoneNubmerHeaderTv, binding.etPhone.text.toString())) {
        //        return@setOnClickListener
        //    }

        //    viewModel.saveChanges(binding.etPhone.text.toString())
        //}
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeEvent()
                    .collect {
                        when (it) {
                            is NotificationSettingsViewModel.NotSettingsEvents.Success -> {
                                requireActivity().snack(it.message)
                            }
                            is NotificationSettingsViewModel.NotSettingsEvents.Failure -> {
                                requireActivity().snack(it.message)
                            }
                        }

                    }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeUiState()
                    .collect {
                        if (it.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        //binding.etPhone.setText(
                        //    it.data.item?.notSettingsData?.myPhone?.active?.convertPhoneToBaseFormat()
                        //        ?.convertPhoneToFullFormat() ?: ""
                        //)

                        notSettingsController.submitList(
                            it.data.item?.notSettingsData?.settingsList ?: emptyList()
                        )

                        showError(it.error)
                    }
            }
        }
    }

    private fun validatePhone(name: TextView, input: String) : Boolean {
        if (input.isEmpty()) return true
        return when(FieldValidationsSettings.PHONE_REGEX.matches(input)) {
            false -> {
                name.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                false
            }
            true -> {
                name.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
                true
            }
        }
    }

}