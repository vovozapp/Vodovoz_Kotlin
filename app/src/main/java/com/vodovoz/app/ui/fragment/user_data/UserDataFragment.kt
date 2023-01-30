package com.vodovoz.app.ui.fragment.user_data

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSelectionGenderBinding
import com.vodovoz.app.databinding.FragmentUserDataBinding
import com.vodovoz.app.ui.adapter.GendersAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.FiledValidationsExtensions.setEmailValidation
import com.vodovoz.app.ui.extensions.FiledValidationsExtensions.setNameValidation
import com.vodovoz.app.ui.extensions.FiledValidationsExtensions.setPasswordValidation
import com.vodovoz.app.ui.extensions.FiledValidationsExtensions.setPhoneValidation
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.UserDataUI
import com.vodovoz.app.util.LogSettings
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToBaseFormat
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToFullFormat
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*

enum class Gender(
    val genderName: String
) {
    MALE("Мужской"),
    FEMALE("Женский")
}

@AndroidEntryPoint
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

}

class GenderSelectionBS : BottomSheetDialogFragment() {

    companion object {
        public const val SELECTED_GENDER = "SELECTED_GENDER"
    }

    private lateinit var binding: BsSelectionGenderBinding

    private val gendersAdapter = GendersAdapter()
    private val genderList = listOf(Gender.MALE, Gender.FEMALE)
    private lateinit var selectedGender: Gender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        selectedGender = Gender.valueOf(GenderSelectionBSArgs.fromBundle(requireArguments()).selectedGender)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BsSelectionGenderBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        setupGendersRecycler()
    }

    private fun setupGendersRecycler() {
        binding.rvGenders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGenders.adapter = gendersAdapter
        val space8 = resources.getDimension(R.dimen.space_8).toInt()
        binding.rvGenders.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.top = space8
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space8
        }
        gendersAdapter.setupListeners { gender ->
            findNavController().previousBackStackEntry?.savedStateHandle?.set(SELECTED_GENDER, gender.name)
            dismiss()
        }
        gendersAdapter.updateData(
            selectedGender = selectedGender,
            genderList = genderList
        )
    }

}