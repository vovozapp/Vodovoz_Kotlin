package com.vodovoz.app.feature.profile.userdata

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentUserDataFlowBinding
import com.vodovoz.app.ui.fragment.user_data.Gender
import com.vodovoz.app.ui.fragment.user_data.GenderSelectionBS
import com.vodovoz.app.ui.fragment.user_data.UserDataFragmentDirections
import com.vodovoz.app.ui.fragment.user_data.UserDataViewModel
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToBaseFormat
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToFullFormat
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class UserDataFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_user_data_flow

    private val viewModel: UserDataFlowViewModel by viewModels()

    private val binding: FragmentUserDataFlowBinding by viewBinding {
        FragmentUserDataFlowBinding.bind(contentView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchUserData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar("Мои данные")
        observeResultLiveData()
        observeUiState()
        observeEvents()
        bindButtons()
    }

    private fun bindButtons() {
        binding.vGender.setOnClickListener {
            viewModel.navigateToGenderChoose()
        }

        binding.vEmail.setOnClickListener {
            requireActivity().snack("Это поле нельзя изменить!")
        }

        binding.btnSave.setOnClickListener {

        }

        binding.vBirthday.setOnClickListener {
            viewModel.onBirthdayClick()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel
                .observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    if (state.data.item != null) {
                        with(binding) {
                            Glide.with(requireContext())
                                .load(state.data.item.avatar)
                                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.png_default_avatar))
                                .into(avatar)

                            etFirstName.setText(state.data.item.firstName)
                            etSecondName.setText(state.data.item.secondName)
                            etEmail.setText(state.data.item.email)
                            etBirthday.setText(state.data.item.birthday)
                            when (state.data.item.birthday == "Не указано") {
                                false -> binding.tilBirthday.hint = "Дата рождения(нельзя менять)"
                                true ->  binding.tilBirthday.hint = "Дата рождения(можно изменить 1 раз!)"
                            }
                            etGender.setText(state.data.item.gender.genderName)
                            etPhone.setText(state.data.item.phone.convertPhoneToBaseFormat().convertPhoneToFullFormat())
                        }
                    }

                    showError(state.error)
                }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel
                .observeEvent()
                .collect {
                    when(it) {
                        is UserDataFlowViewModel.UserDataEvents.NavigateToGenderChoose -> {
                            findNavController().navigate(UserDataFragmentDirections.actionToGenderSelectionBS(it.gender))
                        }
                        is UserDataFlowViewModel.UserDataEvents.UpdateUserDataEvent -> {
                            requireActivity().snack(it.message)
                        }
                        is UserDataFlowViewModel.UserDataEvents.ShowDatePicker -> {
                            val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                                .setTitleText("Дата рождения")
                                .setPositiveButtonText("Ок")
                                .setNegativeButtonText("Назад")
                                .build()

                            datePickerDialog.addOnPositiveButtonClickListener {
                                Calendar.getInstance().apply {
                                    timeInMillis = it
                                    val birthDay = StringBuilder()
                                        .append(when(get(Calendar.DAY_OF_MONTH) < 10) {
                                            true -> "0${get(Calendar.DAY_OF_MONTH)}"
                                            else -> get(Calendar.DAY_OF_MONTH)
                                        })
                                        .append(".")
                                        .append(when(get(Calendar.MONTH) < 10) {
                                            true -> "0${get(Calendar.MONTH)}"
                                            else -> get(Calendar.MONTH)
                                        })
                                        .append(".")
                                        .append(get(Calendar.YEAR))
                                        .toString()

                                    binding.etBirthday.setText(birthDay)
                                }
                            }

                            datePickerDialog.show(childFragmentManager, datePickerDialog::class.simpleName)
                        }
                    }
                }
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(GenderSelectionBS.SELECTED_GENDER)?.observe(viewLifecycleOwner) { gender ->
               viewModel.setUserGender(Gender.valueOf(gender))
            }
    }
}