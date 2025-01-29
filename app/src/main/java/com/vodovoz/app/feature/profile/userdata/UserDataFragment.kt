package com.vodovoz.app.feature.profile.userdata

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.media.ImagePickerFragment
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.databinding.FragmentUserDataFlowBinding
import com.vodovoz.app.feature.alert_dialog.AlertDialogFragment
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToBaseFormat
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToFullFormat
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class Gender(
    val genderName: String,
) {
    MALE("Мужской"),
    FEMALE("Женский")
}

@AndroidEntryPoint
class UserDataFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_user_data_flow

    private val viewModel: UserDataFlowViewModel by viewModels()

    private val profileViewModel: ProfileFlowViewModel by activityViewModels()

    private val userDataValidatingListener = MutableStateFlow(
        UserDataValid(
            eMailValid = false,
            lastNameValid = false,
            nameValid = false
        )
    )
    private val userDataValidating = userDataValidatingListener.asStateFlow()

    @Inject
    lateinit var permissionsControllerFactory: PermissionsController.Factory
    private val permissionsController by lazy { permissionsControllerFactory.create(requireActivity()) }

    private val binding: FragmentUserDataFlowBinding by viewBinding {
        FragmentUserDataFlowBinding.bind(contentView)
    }

    private var sex = ""
    private var birthday = ""
    private var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchUserData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserDataValid()

        initToolbarMyData(
            onLogout = {
                showLogoutDialog()
            }
        )
        observeResultLiveData()
        observeUiState()
        observeEvents()
        bindButtons()
        bindTextListeners()
    }

    private fun bindButtons() {

        binding.downloadAvatar.setOnClickListener {
            permissionsController.methodRequiresStoragePermission {
                findNavController().navigate(
                    R.id.imagePickerFragment,
                    bundleOf(ImagePickerFragment.IMAGE_PICKER_RECEIVER to ImagePickerFragment.AVATAR)
                )
            }
        }

        binding.tvDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

//        binding.vEmail.setOnClickListener {
//            //requireActivity().snack("Это поле нельзя изменить!")
//        }

        binding.btnSave.setOnClickListener {

            val firstName = binding.etName.text ?: return@setOnClickListener
            val secondName = binding.etLastName.text ?: return@setOnClickListener

            if (validateEmail().not()) {
                return@setOnClickListener
            }

            viewModel.updateUserData(
                firstName = firstName.toString(),
                secondName = secondName.toString(),
                sex = sex,
                birthday = birthday,
                email = binding.etEmail.text.toString(),
                phone = phone,
                password = "123456"
            )

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
                        val tvIDText =
                            "${resources.getString(R.string.your_id)} ${state.data.item.id}"
                        with(binding) {
                            Glide.with(requireContext())
                                .load(state.data.item.avatar)
                                .placeholder(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.png_default_avatar
                                    )
                                )
                                .into(avatar)

                            tvID.text = tvIDText
                            etName.setText(state.data.item.firstName)
                            etLastName.setText(state.data.item.secondName)
                            etEmail.setText(state.data.item.email)
                            birthday = (state.data.item.birthday)
                            sex = (state.data.item.gender.genderName)
                            phone = (state.data.item.phone.convertPhoneToBaseFormat()
                                .convertPhoneToFullFormat())
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
                    when (it) {
                        is UserDataFlowViewModel.UserDataEvents.NavigateToGenderChoose -> {
                            findNavController().navigate(
                                UserDataFragmentDirections.actionToGenderSelectionBS(
                                    it.gender
                                )
                            )
                        }

                        is UserDataFlowViewModel.UserDataEvents.UpdateUserDataEvent -> {
                            profileViewModel.refresh()
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
                                        .append(
                                            when (get(Calendar.DAY_OF_MONTH) < 10) {
                                                true -> "0${get(Calendar.DAY_OF_MONTH)}"
                                                else -> get(Calendar.DAY_OF_MONTH)
                                            }
                                        )
                                        .append(".")
                                        .append(
                                            when (get(Calendar.MONTH) < 10) {
                                                true -> "0${get(Calendar.MONTH)}"
                                                else -> get(Calendar.MONTH)
                                            }
                                        )
                                        .append(".")
                                        .append(get(Calendar.YEAR))
                                        .toString()
                                }
                            }

                            datePickerDialog.show(
                                childFragmentManager,
                                datePickerDialog::class.simpleName
                            )
                        }

                        is UserDataFlowViewModel.UserDataEvents.UpdateProfile -> {
                            profileViewModel.refresh()
                        }

                        UserDataFlowViewModel.UserDataEvents.Logout -> {
                            findNavController().popBackStack(R.id.profileFragment, true)
                        }
                    }
                }
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(GenderSelectionBS.SELECTED_GENDER)
            ?.observe(viewLifecycleOwner) { gender ->
                viewModel.setUserGender(Gender.valueOf(gender))
            }
    }

    private fun validateEmail(): Boolean {
        return FieldValidationsSettings.EMAIL_REGEX.matches(binding.etEmail.text.toString())
    }

    private fun bindTextListeners() {
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            userDataValidatingListener.value = userDataValidatingListener.value.copy(
                eMailValid = validateEmail()
            )
        }

        binding.etLastName.doOnTextChanged { lastName, _, _, _ ->
            if (lastName != null) {
                if (lastName.isNotEmpty())
                    userDataValidatingListener.value = userDataValidatingListener.value.copy(
                        lastNameValid = true
                    )
                else userDataValidatingListener.value = userDataValidatingListener.value.copy(
                    lastNameValid = false
                )
            }
        }

        binding.etName.doOnTextChanged { name, _, _, _ ->
            if (name != null) {
                if (name.isNotEmpty())
                    userDataValidatingListener.value = userDataValidatingListener.value.copy(
                        nameValid = true
                    )
                else userDataValidatingListener.value = userDataValidatingListener.value.copy(
                    nameValid = false
                )
            }
        }
    }

    private fun observeUserDataValid() {
        lifecycleScope.launch {
            userDataValidating.collect { userDataValid ->
                with(userDataValid) {
                    if (eMailValid && lastNameValid && nameValid) {
                        binding.btnSave.visibility = View.VISIBLE
                        binding.btnSaveDisabled.visibility = View.GONE
                    } else {
                        binding.btnSave.visibility = View.GONE
                        binding.btnSaveDisabled.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showDeleteAccountDialog(){
        AlertDialogFragment.newInstance(
            title = getString(R.string.delete_account),
            description = getString(R.string.delete_account_confirmation),
            positiveText = getString(R.string.delete),
            negativeText = getString(R.string.cancel),
            onPositiveClick = {
                //todo - delete account
            }
        ).show(childFragmentManager, "DeleteAccountDialog")
    }

    private fun showLogoutDialog(){
        AlertDialogFragment.newInstance(
            title = getString(R.string.exit),
            description = getString(R.string.exit_confirmation),
            positiveText = getString(R.string.exit),
            negativeText = getString(R.string.cancel),
            onPositiveClick = {
                lifecycleScope.launch {
                    profileViewModel.logout().join()
                    findNavController().navigate(UserDataFragmentDirections.actionBackToProfile())
                }
            }
        ).show(childFragmentManager, "LogoutDialog")
    }
}

data class UserDataValid(
    val eMailValid: Boolean,
    val lastNameValid: Boolean,
    val nameValid: Boolean,
)
