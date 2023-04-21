package com.vodovoz.app.ui.fragment.ordering

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentOrderingBinding
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.extensions.ContextExtensions.getDeviceInfo
import com.vodovoz.app.ui.extensions.Date
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.ui.extensions.ViewExtensions.openLink
import com.vodovoz.app.feature.addresses.AddressesFragment
import com.vodovoz.app.feature.addresses.OpenMode
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.model.FreeShippingDaysInfoBundleUI
import com.vodovoz.app.ui.model.PayMethodUI
import com.vodovoz.app.ui.model.ShippingIntervalUI
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI
import com.vodovoz.app.util.FieldValidationsSettings
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class OrderingFragment : ViewStateBaseFragment() {

    companion object {
        const val SELECTED_PAY_METHOD = "SELECTED_PAY_METHOD"
        const val SELECTED_SHIPPING_INTERVAL = "SELECTED_SHIPPING_INTERVAL"
        const val SELECTED_SHIPPING_ALERT = "SELECTED_SHIPPING_ALERT"
    }

    private lateinit var binding: FragmentOrderingBinding
    private val viewModel: OrderingViewModel by viewModels()

    private var trackErrors: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        OrderingFragmentArgs.fromBundle(requireArguments()).let { args ->
            viewModel.updateArgs(args.full, args.deposit, args.discount, args.total, args.lastActualCart, args.coupon)
        }
    }

    override fun update() {}

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentOrderingBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        clearFields()
        onStateSuccess()
        setupActionBar()
        setupButtons()
        observeViewModel()
        observeResultLiveData()
        setupTextWatchers()
    }

    private fun setupActionBar() {
        binding.incAppBar.tvTitle.text = getString(R.string.ordering_title_text)
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.nsvContent.setScrollElevation(binding.ab)
    }

    private fun setupTextWatchers() {
        binding.etName.doAfterTextChanged {
            viewModel.name = it.toString()
            if (trackErrors) validateSimpleField(binding.tvNameName, it.toString())
        }
        binding.etCompanyName.doAfterTextChanged {
            viewModel.companyName = it.toString()
            if (trackErrors) validateSimpleField(binding.tvNameCompanyName, it.toString())
        }
        binding.etPhone.setPhoneValidator {
            viewModel.phone = it.toString()
            if (trackErrors) validatePhone(binding.tvNamePhone, it.toString())
        }

        binding.etEmail.doAfterTextChanged {
            viewModel.email = it.toString()
            if (trackErrors) validateEmail(binding.tvNameEmail, it.toString())
        }
        binding.etComment.doAfterTextChanged { viewModel.comment = it.toString() }
        binding.etInputCash.doAfterTextChanged {
            if (viewModel.selectedPayMethodUI?.id == 1L) {
                if (it.toString().isNotEmpty()) viewModel.inputCash = it.toString().toInt()
                if (trackErrors) validateSimpleField(binding.tvNamePayMethod, it.toString())
            }
        }
        binding.scOperatorCall.setOnCheckedChangeListener { _, isChecked ->
            viewModel.needOperatorCall = isChecked
        }
    }

    private fun setupButtons() {
        binding.btnOrder.setOnClickListener { validateForm() }
        binding.btnPersonal.setOnClickListener {
            if (viewModel.selectedOrderType != OrderType.PERSONAL) {
                viewModel.clearData()
                viewModel.selectedOrderType = OrderType.PERSONAL
                binding.btnCompany.setBackgroundResource(R.drawable.selector_bkg_button_gray_rect)
                binding.btnPersonal.setBackgroundResource(R.drawable.bkg_button_blue_rect_disabled)
                binding.llCompanyNameContainer.visibility = View.GONE
                clearFields()
            }
        }

        binding.btnCompany.setOnClickListener {
            if (viewModel.selectedOrderType != OrderType.COMPANY) {
                viewModel.clearData()
                viewModel.selectedOrderType = OrderType.COMPANY
                binding.btnPersonal.setBackgroundResource(R.drawable.selector_bkg_button_gray_rect)
                binding.btnCompany.setBackgroundResource(R.drawable.bkg_button_blue_rect_disabled)
                binding.llCompanyNameContainer.visibility = View.VISIBLE
                clearFields()
            }
        }
        binding.tvAddress.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.orderingFragment) {
                findNavController().navigate(OrderingFragmentDirections.actionToSavedAddressesDialogFragment(
                    OpenMode.SelectAddress.name,
                    viewModel.selectedOrderType.name
                ))
            }
        }
        binding.tvFreeShipping.setOnClickListener {
            when (viewModel.freeShippingDaysInfoBundleUI) {
                null -> viewModel.fetchFreeShippingDaysInfo()
                else -> showFreeShippingDaysInfoPopup(viewModel.freeShippingDaysInfoBundleUI!!)
            }
        }
        binding.tvDate.setOnClickListener {
            when(viewModel.selectedAddressUI) {
                null -> Snackbar.make(binding.root, "Выберите адрес!", Snackbar.LENGTH_LONG).show()
                else -> showDatePickerDialog()
            }
        }
        binding.tvShippingInterval.setOnClickListener {
            viewModel.waitToShowIntervalSelection = true
            viewModel.fetchShippingInfo()
        }
        binding.tvPayMethod.setOnClickListener {
            viewModel.waitToShowPayMethodSelection = true
            viewModel.fetchShippingInfo()
        }
        binding.scShippingAlert.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                if (findNavController().currentDestination?.id == R.id.orderingFragment) {
                    findNavController().navigate(OrderingFragmentDirections.actionToShippingAlertsSelectionBS(
                        viewModel.fetchShippingAlertsList().toTypedArray()
                    ))
                }
            }
        }
    }

    private fun clearFields() {
        trackErrors = false
        viewModel.clearData()
        binding.root.clearFocus()
        binding.tvNameAddress.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.tvNameCompanyName.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.tvNameName.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.tvNameEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.tvNamePhone.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.tvNameDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.tvNamePayMethod.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        binding.etCompanyName.text = null
        binding.etName.text = null
        binding.etEmail.text = null
        binding.tvAddress.text = "Адрес"
        binding.tvDate.text = "Дата"
        binding.etPhone.setText("")
        binding.tvShippingInterval.text = "Время"
        binding.tvShippingPrice.setPriceText(0)
        binding.tvParkingPrice.setPriceText(0)
        binding.tvPayMethod.text = "Выберите способ оплаты"
        binding.etInputCash.text = null
        binding.mtBetweenPayMethodAndInputCash.visibility = View.GONE
        binding.etInputCash.visibility = View.GONE
        binding.etComment.text = null
    }

    private fun observeViewModel() {
        viewModel.freeShippingDaysInfoLD.observe(viewLifecycleOwner) { showFreeShippingDaysInfoPopup(it) }
        viewModel.errorLD.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }
        viewModel.payMethodUIListLD.observe(viewLifecycleOwner) { payMethodUIList ->
            if (viewModel.waitToShowPayMethodSelection) {
                viewModel.waitToShowPayMethodSelection = false
                showPayMethodPopup(
                    payMethodUIList = payMethodUIList,
                    selectedPayMethodId = viewModel.selectedPayMethodUI?.id ?: payMethodUIList.first().id
                )
            }
        }
        viewModel.shippingIntervalUiListLD.observe(viewLifecycleOwner) { shippingIntervalUIList ->
            if (viewModel.waitToShowIntervalSelection) {
                viewModel.waitToShowIntervalSelection = false
                showShippingIntervalSelectionPopup(shippingIntervalUIList)
            }
        }
        viewModel.fullPriceLD.observe(viewLifecycleOwner) { binding.tvFullPrice.setPriceText(it) }
        viewModel.depositPriceLD.observe(viewLifecycleOwner) { binding.tvDepositPrice.setPriceText(it) }
        viewModel.discountPriceLD.observe(viewLifecycleOwner) { binding.tvDiscountPrice.setPriceText(it, true) }
        viewModel.shippingPriceLD.observe(viewLifecycleOwner) { binding.tvShippingPrice.setPriceText(it) }
        viewModel.parkingPriceLD.observe(viewLifecycleOwner) { binding.tvParkingPrice.setPriceText(it) }
        viewModel.totalPriceLD.observe(viewLifecycleOwner) {
            binding.btnOrder.text = String.format(getString(R.string.order_btn_text), it)
            binding.tvTotalPrice.setPriceText(it)
        }
        viewModel.cartChangeMessageLD.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(it)
                .setPositiveButton("Ок") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        viewModel.todayShippingMessageLD.observe(viewLifecycleOwner) {
            if (viewModel.waitToShowTodayShippingInfo) {
                viewModel.waitToShowTodayShippingInfo = false
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(it)
                    .setPositiveButton("Ок") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        viewModel.orderingCompletedInfoBundleUILD.observe(viewLifecycleOwner) { orderingCompleted(it) }
    }

    private fun orderingCompleted(orderingCompletedInfoBundleUI: OrderingCompletedInfoBundleUI) {
        binding.ab.translationZ = 0f
        binding.nsvContent.visibility = View.INVISIBLE
        binding.tvOrderId.text = orderingCompletedInfoBundleUI.message
        binding.llOrderingCompleted.visibility = View.VISIBLE
        when(orderingCompletedInfoBundleUI.paymentURL.isNotEmpty()) {
            true -> {
                binding.btnGoToPayment.visibility = View.VISIBLE
                binding.btnGoToPayment.setOnClickListener { binding.root.openLink(orderingCompletedInfoBundleUI.paymentURL) }
            }
            false -> binding.btnGoToPayment.visibility = View.INVISIBLE
        }
        binding.incAppBar.tvTitle.text = "Спасибо за заказ"
    }

    private fun showPayMethodPopup(payMethodUIList: List<PayMethodUI>, selectedPayMethodId: Long) {
        if (findNavController().currentDestination?.id == R.id.orderingFragment) {
            findNavController().navigate(OrderingFragmentDirections.actionToPayMethodSelectionBS(
                payMethodUIList.toTypedArray(),
                selectedPayMethodId
            ))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(SELECTED_PAY_METHOD)?.observe(viewLifecycleOwner) { payMethodId ->
                viewModel.selectedPayMethodUI = viewModel.shippingInfoBundleUI!!.payMethodUIList.find { it.id == payMethodId }
                binding.tvPayMethod.text = viewModel.selectedPayMethodUI?.name
                if (payMethodId == 1L) {
                    binding.etInputCash.visibility = View.VISIBLE
                    binding.mtBetweenPayMethodAndInputCash.visibility = View.VISIBLE
                } else {
                    binding.etInputCash.visibility = View.GONE
                    binding.mtBetweenPayMethodAndInputCash.visibility = View.GONE
                    binding.tvNamePayMethod.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
                    binding.etInputCash.text = null
                }
        }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(SELECTED_SHIPPING_INTERVAL)?.observe(viewLifecycleOwner) { shippingIntervalId ->
                viewModel.selectedShippingIntervalUI = viewModel.shippingInfoBundleUI!!.shippingIntervalUIList.find { it.id == shippingIntervalId }
                binding.tvShippingInterval.text = viewModel.selectedShippingIntervalUI?.name.toString()
                binding.tvNameDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
            }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(SELECTED_SHIPPING_ALERT)?.observe(viewLifecycleOwner) { shippingAlertId ->
                viewModel.selectedShippingAlertUI = viewModel.shippingAlertUIList?.find { it.id == shippingAlertId }
            }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<AddressUI>(AddressesFragment.SELECTED_ADDRESS)?.observe(viewLifecycleOwner) { addressUI ->
                viewModel.clearData()
                clearFields()
                viewModel.selectedAddressUI = addressUI
                when(viewModel.selectedOrderType) {
                    OrderType.COMPANY -> binding.llCompanyNameContainer.visibility = View.VISIBLE
                    OrderType.PERSONAL -> binding.llCompanyNameContainer.visibility = View.GONE
                }
                binding.tvAddress.text = addressUI.fullAddress
                binding.etName.setText(addressUI.name)
                binding.etEmail.setText(addressUI.email)
                binding.etPhone.setText(addressUI.phone)
                viewModel.fetchShippingInfo()
            }
    }

    private fun showShippingIntervalSelectionPopup(shippingIntervalUIList: List<ShippingIntervalUI>) {
        if (viewModel.selectedDate == null) {
            Snackbar.make(binding.root, "Выберите дату!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (shippingIntervalUIList.isEmpty()) {
            Snackbar.make(binding.root, "На эту дату нет доставок!", Snackbar.LENGTH_LONG).show()
        } else {
            if (findNavController().currentDestination?.id == R.id.orderingFragment) {
                findNavController().navigate(OrderingFragmentDirections.actionToShippingIntervalSelectionBS(
                    shippingIntervalUIList.toTypedArray()
                ))
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar[Calendar.YEAR]
        val currentMonth = calendar[Calendar.MONTH]
        val currentDay = calendar[Calendar.DAY_OF_MONTH]

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val date = Date.from(day, month, year)
            binding.tvDate.text = viewModel.dateFormatter.format(date)
            viewModel.selectedDate = date
            if (currentYear == year && currentMonth == month && currentDay == day) {
                viewModel.waitToShowTodayShippingInfo = true
                viewModel.fetchShippingInfo()
            }
            binding.tvShippingInterval.text = "Время"
            viewModel.fetchShippingInfo()
        }
        val datePicker = DatePickerDialog(
            requireContext(),
            datePickerListener,
            currentYear, currentMonth, currentDay)

        datePicker.datePicker.minDate = calendar.timeInMillis
        datePicker.show()
    }

    private fun showFreeShippingDaysInfoPopup(freeShippingDaysInfoBundleUI: FreeShippingDaysInfoBundleUI) {
        if (findNavController().currentDestination?.id == R.id.orderingFragment) {
            findNavController().navigate(OrderingFragmentDirections.actionToFreeShippingSaysBS(
                freeShippingDaysInfoBundleUI.title,
                freeShippingDaysInfoBundleUI.info
            ))
        }
    }

    private fun validateSimpleField(name: TextView, input: String) = when(input.isNotEmpty()) {
        false -> {
            name.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            false
        }
        true -> {
            name.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
            true
        }
    }

    private fun validateEmail(name: TextView, input: String) = when(FieldValidationsSettings.EMAIL_REGEX.matches(input)) {
        false -> {
            name.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            false
        }
        true -> {
            name.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
            true
        }
    }

    private fun validatePhone(name: TextView, input: String) = when(FieldValidationsSettings.PHONE_REGEX.matches(input)) {
        false -> {
            name.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            false
        }
        true -> {
            name.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
            true
        }
    }

    private fun validateForm() {
        var isValid = true

        if (!validateSimpleField(binding.tvNameName, binding.etName.text.toString())) isValid = false
        if (!validateEmail(binding.tvNameEmail, binding.etEmail.text.toString())) isValid = false
        if (!validatePhone(binding.tvNamePhone, binding.etPhone.text.toString())) isValid = false
        if (viewModel.selectedOrderType == OrderType.COMPANY) {
            if (!validateSimpleField(binding.tvNameCompanyName, binding.etCompanyName.text.toString())) isValid = false
        }
        if (viewModel.selectedAddressUI == null) {
            isValid = false
            binding.tvNameAddress.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        if (viewModel.selectedDate == null) {
            isValid = false
            binding.tvNameDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        if (viewModel.selectedShippingIntervalUI == null) {
            isValid = false
            binding.tvNameDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        if (viewModel.selectedPayMethodUI == null) {
            isValid = false
            binding.tvNamePayMethod.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        if (isValid) viewModel.checkActualCart(requireContext().getDeviceInfo())
        else {
            trackErrors = true
            Snackbar.make(binding.root, "Пожалуйста, заполните верно все необходимые поля!", Snackbar.LENGTH_LONG).show()
        }
    }

}

enum class OrderType(val value: Int) {
    PERSONAL(1), COMPANY(2)
}

