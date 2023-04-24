package com.vodovoz.app.feature.profile.userdata

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.media.ImagePickerFragment
import com.vodovoz.app.databinding.FragmentUserDataFlowBinding
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToBaseFormat
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToFullFormat
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import com.vodovoz.app.util.extensions.textOrErrorWithEmpty
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

enum class Gender(
    val genderName: String
) {
    MALE("Мужской"),
    FEMALE("Женский")
}

/*@AndroidEntryPoint
class UserDataFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentUserDataBinding
    private val viewModel: UserDataViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()
    private val trackErrorSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validEmailSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validPhoneSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validFirstNameSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validSecondNameSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validPasswordSubject: PublishSubject<Boolean> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateData()
        subscribeSubjects()
    }

    private fun subscribeSubjects() {
        validEmailSubject.subscribeBy { viewModel.validEmail = it }.addTo(compositeDisposable)
        validPhoneSubject.subscribeBy { viewModel.validPhone = it }.addTo(compositeDisposable)
        validFirstNameSubject.subscribeBy { viewModel.validFirstName = it }.addTo(compositeDisposable)
        validSecondNameSubject.subscribeBy { viewModel.validSecondName = it }.addTo(compositeDisposable)
        validPasswordSubject.subscribeBy { viewModel.validPassword = it }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentUserDataBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initAppBar()
        initButtons()
        setupFields()
        observeViewModel()
        observeResultLiveData()
    }

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = "Мои данные"
        binding.incAppBar.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupFields() {
        binding.etEmail.setEmailValidation(binding.tilEmail, compositeDisposable, trackErrorSubject, validEmailSubject)
        binding.etPhone.setPhoneValidation(binding.tilPhone, compositeDisposable, trackErrorSubject, validPhoneSubject)
        binding.etFirstName.setNameValidation(binding.tilFirstName, compositeDisposable, trackErrorSubject, validFirstNameSubject)
        binding.etSecondName.setNameValidation(binding.tilSecondName, compositeDisposable, trackErrorSubject, validSecondNameSubject)
        binding.etPassword.setPasswordValidation(binding.tilPassword, compositeDisposable, trackErrorSubject, validPasswordSubject)
    }

    private fun initButtons() {
        binding.swContentContainer.setScrollElevation(binding.incAppBar.root)

        binding.btnSave.setOnClickListener {
            trackErrorSubject.onNext(true)
            Log.d(LogSettings.ID_LOG, "${viewModel.validEmail} ${viewModel.validPhone} ${viewModel.validFirstName} ${viewModel.validSecondName}")
            when(
                viewModel.validEmail &&
                viewModel.validPhone &&
                viewModel.validFirstName &&
                viewModel.validSecondName &&
                viewModel.validPassword
            ) {
                true -> viewModel.updateUserData(
                    firstName = binding.etFirstName.text.toString(),
                    secondName = binding.etSecondName.text.toString(),
                    gender = binding.etGender.text.toString(),
                    birthday = binding.etBirthday.text.toString(),
                    email = binding.etEmail.text.toString(),
                    phone = binding.etPhone.text.toString(),
                    password = binding.etPassword.text.toString()
                )
                false -> {
                    Snackbar.make(binding.root, "Проверьте правильность введенных данных!", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.vGender.setOnClickListener {
            findNavController().navigate(UserDataFragmentDirections.actionToGenderSelectionBS(viewModel.userDataUI.gender.name))
        }

        binding.vEmail.setOnClickListener {
            Snackbar.make(binding.root, "Это поле нельзя изменить!", Snackbar.LENGTH_SHORT).show()
        }

        binding.vBirthday.setOnClickListener {
            if (viewModel.userDataUI.birthday == "Не указано" && viewModel.canChangeBirthday){
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
            } else {
                Snackbar.make(binding.root, "Это поле нельзя изменить!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(GenderSelectionBS.SELECTED_GENDER)?.observe(viewLifecycleOwner) { gender ->
                viewModel.userDataUI.gender = Gender.valueOf(gender)
            }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.userDataUILD.observe(viewLifecycleOwner) { userDataUI ->
            fillUserData(userDataUI)
        }

        viewModel.messageLD.observe(viewLifecycleOwner) { message ->
            when (binding.etBirthday.text.toString() == "Не указано") {
                false -> binding.tilBirthday.hint = "Дата рождения(нельзя менять)"
                true ->  binding.tilBirthday.hint = "Дата рождения(можно изменить 1 раз!)"
            }
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun fillUserData(userDataUI: UserDataUI) {
        with(binding) {
            Glide.with(requireContext())
                .load(userDataUI.avatar)
                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.png_default_avatar))
                .into(avatar)

            etFirstName.setText(userDataUI.firstName)
            etSecondName.setText(userDataUI.secondName)
            etEmail.setText(userDataUI.email)
            etBirthday.setText(userDataUI.birthday)
            when (userDataUI.birthday == "Не указано") {
                false -> binding.tilBirthday.hint = "Дата рождения(нельзя менять)"
                true ->  binding.tilBirthday.hint = "Дата рождения(можно изменить 1 раз!)"
            }
            etGender.setText(userDataUI.gender.genderName)
            etPhone.setText(userDataUI.phone.convertPhoneToBaseFormat().convertPhoneToFullFormat())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}*/

@AndroidEntryPoint
class UserDataFragment : BaseFragment() {

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
        bindTextListeners()
        binding.etPhone.setPhoneValidator {}
    }

    private fun bindButtons() {

        binding.downloadAvatar.setOnClickListener {
            debugLog { "spasibo clcick" }
            findNavController().navigate(R.id.imagePickerFragment, bundleOf(ImagePickerFragment.IMAGE_PICKER_RECEIVER to ImagePickerFragment.AVATAR))
        }


        binding.vGender.setOnClickListener {
            viewModel.navigateToGenderChoose()
        }

        binding.vEmail.setOnClickListener {
            requireActivity().snack("Это поле нельзя изменить!")
        }

        binding.btnSave.setOnClickListener {

            val firstName = binding.tilFirstName.textOrErrorWithEmpty(2) ?: return@setOnClickListener
            val secondName = binding.tilSecondName.textOrErrorWithEmpty(2) ?: return@setOnClickListener

            val validateEmail = validateEmail()
            if (validateEmail.not()) {
                return@setOnClickListener
            }

            if (binding.etPhone.text.isNullOrEmpty().not()) {
                if (FieldValidationsSettings.PHONE_REGEX.matches(binding.etPhone.text.toString())
                        .not()
                ) {
                    return@setOnClickListener
                }
            }

            val password = binding.tilPassword.textOrErrorWithEmpty(2) ?: return@setOnClickListener

            val birthday = if (binding.etBirthday.text.toString() == "Не указано") {
                ""
            } else {
                binding.etBirthday.text.toString()
            }

            viewModel.updateUserData(
                firstName = firstName,
                secondName = secondName,
                sex = binding.etGender.text.toString(),
                birthday = birthday,
                email = binding.etEmail.text.toString(),
                phone = binding.etPhone.text.toString(),
                password = password
            )

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

    private fun validateEmail(): Boolean {
        if (!FieldValidationsSettings.EMAIL_REGEX.matches(binding.etEmail.text.toString())) {
            binding.tilEmail.error = "Неправильный формат почты"
            return false
        } else  binding.tilEmail.error = null
        return true
    }

    private fun bindTextListeners() {
        binding.etEmail.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilEmail.isErrorEnabled = false
        }

        binding.etPassword.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilPassword.isErrorEnabled = false
        }

        binding.etFirstName.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilFirstName.isErrorEnabled = false
        }

        binding.etSecondName.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilSecondName.isErrorEnabled = false
        }

        binding.etPhone.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilPhone.isErrorEnabled = false
        }

        binding.etPhone.setPhoneValidator {
            when(FieldValidationsSettings.PHONE_REGEX.matches(it.toString())) {
                true -> binding.tilPhone.error = null
                false -> binding.tilPhone.error = "Неверный формат телефона"
            }
        }
    }
}

