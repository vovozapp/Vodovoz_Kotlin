package com.vodovoz.app.ui.fragment.user_data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentUserDataBinding
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.UserDataUI
import java.util.*

class UserDataFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentUserDataBinding
    private lateinit var viewModel: UserDataViewModel

    private lateinit var bsbChooseSex: BottomSheetBehavior<*>

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
        initBSChooseSex()
        initButtons()
        observeViewModel()
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

    private fun initBSChooseSex() {
        bsbChooseSex = BottomSheetBehavior.from(binding.bsChooseSex.root)
        bsbChooseSex.state = BottomSheetBehavior.STATE_HIDDEN

        bsbChooseSex.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset != 0f) {
                        binding.bottomSheetBackgroundLayer.visibility = View.INVISIBLE
                    } else {
                        binding.bottomSheetBackgroundLayer.visibility = View.VISIBLE
                    }
                }
            }
        )

        binding.bsChooseSex.men.setOnClickListener {
            binding.sex.text = "Мужской"
            binding.bsChooseSex.men.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackTextDark))
            binding.bsChooseSex.woman.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackTextPrimary))
            bsbChooseSex.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.bsChooseSex.woman.setOnClickListener {
            binding.sex.text = "Женский"
            binding.bsChooseSex.woman.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackTextDark))
            binding.bsChooseSex.men.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackTextPrimary))
            bsbChooseSex.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun initButtons() {
        binding.scrollContainer.setScrollElevation(binding.appBar)

        binding.save.setOnClickListener {
            viewModel.userDataUI.firstName = binding.firstName.text.toString()
            viewModel.userDataUI.secondName = binding.secondName.text.toString()
            viewModel.userDataUI.sex = binding.sex.text.toString()
            viewModel.userDataUI.birthday = binding.birthday.text.toString()
            viewModel.userDataUI.phone = binding.phone.text.toString()

            viewModel.updateUserData(binding.newPassword.text.toString())
        }

        binding.sex.setOnClickListener {
            bsbChooseSex.state = BottomSheetBehavior.STATE_EXPANDED
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
                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.default_avatar))
                .into(avatar)

            firstName.setText(userDataUI.firstName)
            secondName.setText(userDataUI.secondName)
            email.text = userDataUI.email
            birthday.text = userDataUI.birthday
            sex.text = userDataUI.sex
            phone.setText(userDataUI.phone)

            when(userDataUI.sex) {
                "Мужской" -> {
                    binding.bsChooseSex.men.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackTextDark))
                    binding.bsChooseSex.woman.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackTextPrimary))
                }
                "Женский" -> {
                    binding.bsChooseSex.woman.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackTextDark))
                    binding.bsChooseSex.men.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackTextPrimary))
                }
            }
        }
    }

}