package com.vodovoz.app.ui.fragment.user_data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.UserDataUI
import java.util.*

enum class Gender(
    val genderName: String
) {
    MALE("Мужской"),
    FEMALE("Женский")
}

class UserDataFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentUserDataBinding
    private lateinit var viewModel: UserDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[UserDataViewModel::class.java]
        viewModel.updateData()
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
        observeViewModel()
        observeResultLiveData()
    }

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initButtons() {
        binding.scrollContainer.setScrollElevation(binding.appBar)

        binding.save.setOnClickListener {
            viewModel.userDataUI.firstName = binding.firstName.text.toString()
            viewModel.userDataUI.secondName = binding.secondName.text.toString()
            viewModel.userDataUI.birthday = binding.birthday.text.toString()
            viewModel.userDataUI.phone = binding.phone.text.toString()

            viewModel.updateUserData(binding.newPassword.text.toString())
        }

        binding.sex.setOnClickListener {
            findNavController().navigate(UserDataFragmentDirections
                .actionToGenderSelectionBS(viewModel.userDataUI.gender.name))
        }

        binding.birthday.setOnClickListener {
            if (viewModel.userDataUI.birthday == "Не указано") {
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

                        binding.birthday.text = birthDay
                    }
                }

                datePickerDialog.show(childFragmentManager, datePickerDialog::class.simpleName)
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
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun fillUserData(userDataUI: UserDataUI) {
        with(binding) {
            Glide.with(requireContext())
                .load(userDataUI.avatar)
                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.png_default_avatar))
                .into(avatar)

            firstName.setText(userDataUI.firstName)
            secondName.setText(userDataUI.secondName)
            email.text = userDataUI.email
            birthday.text = userDataUI.birthday
            sex.text = userDataUI.gender.genderName
            phone.setText(userDataUI.phone)
        }
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