package com.vodovoz.app.ui.fragment.ordering

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsFreeShippingDaysBinding
import com.vodovoz.app.databinding.BsSelectionPayMethodBinding
import com.vodovoz.app.databinding.FragmentOrderingBinding
import com.vodovoz.app.ui.adapter.FieldType
import com.vodovoz.app.ui.adapter.FormAdapter
import com.vodovoz.app.ui.adapter.FormField
import com.vodovoz.app.ui.adapter.PayMethodsAdapter
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.Date
import com.vodovoz.app.ui.extensions.Date.dd
import com.vodovoz.app.ui.extensions.Date.mm
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.fragment.saved_addresses.AddressesFragment
import com.vodovoz.app.ui.fragment.saved_addresses.OpenMode
import com.vodovoz.app.ui.fragment.user_data.GenderSelectionBS
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.model.FreeShippingDaysInfoBundleUI
import com.vodovoz.app.ui.model.PayMethodUI
import com.vodovoz.app.ui.model.ShippingIntervalUI
import com.vodovoz.app.util.LogSettings
import java.text.SimpleDateFormat
import java.util.*

class OrderingFragment : ViewStateBaseFragment() {

    companion object {
        const val SELECTED_PAY_METHOD = "SELECTED_PAY_METHOD"
        const val SELECTED_SHIPPING_INTERVAL = "SELECTED_SHIPPING_INTERVAL"
        const val SELECTED_SHIPPING_ALERT = "SELECTED_SHIPPING_ALERT"
    }

    private lateinit var binding: FragmentOrderingBinding
    private lateinit var viewModel: OrderingViewModel

    private val orderingAdapter = FormAdapter()
    private lateinit var orderingForm: List<FormField>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[OrderingViewModel::class.java]
    }

    override fun update() {

    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentOrderingBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        onStateSuccess()
        setupActionBar()
        setupOrderingRecycler()
        setupButtons()
        observeViewModel()
        observeResultLiveData()
    }

    private fun setupActionBar() {
        binding.incAppBar.tvTitle.text = getString(R.string.ordering_title_text)
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.nsvContent.setScrollElevation(binding.ab)
    }

    private fun setupOrderingRecycler() {
        binding.rvOrdering.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrdering.adapter = orderingAdapter
        orderingAdapter.setupListeners(
            afterErrorChange = {

            },
            onPromptClick = { onPromptClick(it) },
            onFieldClick = { field, number -> onFieldClick(field, number) },
            onSwitchChange = { onSwitchChange(it) }
        )
        orderingForm = when(viewModel.orderType) {
            OrderType.COMPANY -> buildForm(OrderType.COMPANY)
            OrderType.PERSONAL -> buildForm(OrderType.PERSONAL)
        }
        orderingAdapter.formFieldList = orderingForm
    }

    private fun observeViewModel() {
        viewModel.freeShippingDaysInfoLD.observe(viewLifecycleOwner) { showFreeShippingDaysInfoPopup(it) }
        viewModel.errorLD.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }
        viewModel.payMethodUIListLD.observe(viewLifecycleOwner) { payMethodUIList ->
            showPayMethodPopup(
                payMethodUIList = payMethodUIList,
                selectedPayMethodId = viewModel.selectedPayMethodUI?.id ?: payMethodUIList.first().id
            )
        }
        viewModel.shippingIntervalUiListLD.observe(viewLifecycleOwner) { shippingIntervalUIList ->
            showShippingIntervalSelectionPopup(shippingIntervalUIList)
        }
    }

    private fun showPayMethodPopup(payMethodUIList: List<PayMethodUI>, selectedPayMethodId: Long) {
        findNavController().navigate(OrderingFragmentDirections.actionToPayMethodSelectionBS(
            payMethodUIList.toTypedArray(),
            selectedPayMethodId
        ))
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(SELECTED_PAY_METHOD)?.observe(viewLifecycleOwner) { payMethodId ->
                viewModel.selectedPayMethodUI = viewModel.payMethodUIList?.find { it.id == payMethodId }
                orderingAdapter.formFieldList.find { it.fieldType == FieldType.PAY_METHOD }?.let {
                    (it as FormField.SingleLineField).value = viewModel.selectedPayMethodUI?.name.toString()
                    orderingAdapter.notifyItemChanged(orderingAdapter.formFieldList.indexOf(it))
                }
        }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(SELECTED_SHIPPING_INTERVAL)?.observe(viewLifecycleOwner) { shippingIntervalId ->
                viewModel.selectedShippingIntervalUI = viewModel.shippingIntervalUIList?.find { it.id == shippingIntervalId }
                orderingAdapter.formFieldList.find { it.fieldType == FieldType.DATE }?.let {
                    (it as FormField.DoubleLineField).secondValue = viewModel.selectedShippingIntervalUI?.name.toString()
                    orderingAdapter.notifyItemChanged(orderingAdapter.formFieldList.indexOf(it))
                }
            }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<AddressUI>(AddressesFragment.SELECTED_ADDRESS)?.observe(viewLifecycleOwner) { addressUI ->
                viewModel.clearData()
                orderingForm = buildForm(viewModel.orderType)
                orderingAdapter.formFieldList = orderingForm
                viewModel.selectedAddressUI = addressUI
                orderingForm.find { it.fieldType == FieldType.ADDRESS }?.let {
                    (it as FormField.SingleLineWithPromptField).value = addressUI.fullAddress
                    orderingAdapter.notifyItemChanged(orderingAdapter.formFieldList.indexOf(it))
                }

                orderingForm.find { it.fieldType == FieldType.EMAIL }?.let {
                    (it as FormField.SingleLineField).value = addressUI.email
                    orderingAdapter.notifyItemChanged(orderingAdapter.formFieldList.indexOf(it))
                }

                orderingForm.find { it.fieldType == FieldType.PHONE }?.let {
                    (it as FormField.SingleLineField).value = addressUI.phone
                    orderingAdapter.notifyItemChanged(orderingAdapter.formFieldList.indexOf(it))
                }

                orderingForm.find { it.fieldType == FieldType.NAME }?.let {
                    (it as FormField.SingleLineField).value = addressUI.name
                    orderingAdapter.notifyItemChanged(orderingAdapter.formFieldList.indexOf(it))
                }
            }
    }

    private fun onPromptClick(formField: FormField) {
        when(formField.fieldType) {
            FieldType.ADDRESS -> {
                when (viewModel.freeShippingDaysInfoBundleUI) {
                    null -> viewModel.fetchFreeShippingDaysInfo()
                    else -> showFreeShippingDaysInfoPopup(viewModel.freeShippingDaysInfoBundleUI!!)
                }
            }
        }
    }

    private fun onSwitchChange(formField: FormField) {
        when(formField.fieldType) {
            FieldType.ALERT_DRIVER -> {
                //findNavController().navigate(OrderingFragmentDirections.)
            }
        }
    }

    private fun onFieldClick(formField: FormField, fieldNumber: Int?) {
        when(formField.fieldType) {
            FieldType.ADDRESS -> {
                findNavController().navigate(OrderingFragmentDirections.actionToSavedAddressesDialogFragment().apply {
                    this.openMode = OpenMode.SelectAddress.name
                    this.addressType = viewModel.orderType.name
                })
            }
            FieldType.DATE -> {
                when(fieldNumber) {
                    1 -> when(viewModel.selectedAddressUI) {
                        null -> Snackbar.make(binding.root, "Выберите адрес!", Snackbar.LENGTH_LONG).show()
                        else -> showDatePickerDialog()
                    }
                    2 -> when(viewModel.shippingIntervalUIList) {
                        null -> viewModel.fetchShippingIntervalList()
                        else -> showShippingIntervalSelectionPopup(viewModel.shippingIntervalUIList!!)
                    }
                }
            }
            FieldType.PAY_METHOD -> {
                when(viewModel.payMethodUIList) {
                    null -> viewModel.fetchPayMethods()
                    else -> showPayMethodPopup(
                        payMethodUIList = viewModel.payMethodUIList!!,
                        selectedPayMethodId = viewModel.selectedPayMethodUI?.id ?: viewModel.payMethodUIList!!.first().id
                    )
                }
            }
        }
    }

    private fun showShippingIntervalSelectionPopup(shippingIntervalUIList: List<ShippingIntervalUI>) {
        if (shippingIntervalUIList.isEmpty()) {
            Snackbar.make(binding.root, "На эту дату нет доставок!", Snackbar.LENGTH_LONG).show()
        } else {
            findNavController().navigate(OrderingFragmentDirections.actionToShippingIntervalSelectionBS(
                shippingIntervalUIList.toTypedArray()
            ))
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar[Calendar.YEAR]
        val currentMonth = calendar[Calendar.MONTH]
        val currentDay = calendar[Calendar.DAY_OF_MONTH]

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val date = Date.from(day, month, year)
            orderingForm.find { it.fieldType == FieldType.DATE }?.let {
                (it as FormField.DoubleLineField).firstValue = "${date.dd()}.${date.mm()}.$year"
                orderingAdapter.notifyItemChanged(orderingAdapter.formFieldList.indexOf(it))
            }
            viewModel.selectedDate = date
//            if (currentYear == year && currentMonth == month && currentDay == day) {
//                MaterialAlertDialogBuilder(requireContext())
//                    .setMessage(dialogMessage)
//                    .setPositiveButton("Ок") { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                    .show()
//            }
        }
        val datePicker = DatePickerDialog(
            requireContext(),
            datePickerListener,
            currentYear, currentMonth, currentDay)

        datePicker.datePicker.minDate = calendar.timeInMillis
        datePicker.show()
    }

    private fun showFreeShippingDaysInfoPopup(freeShippingDaysInfoBundleUI: FreeShippingDaysInfoBundleUI) {
        findNavController().navigate(OrderingFragmentDirections.actionToFreeShippingSaysBS(
            freeShippingDaysInfoBundleUI.title,
            freeShippingDaysInfoBundleUI.info
        ))
    }

    private fun setupButtons() {
        binding.btnPersonal.setOnClickListener {
            if (viewModel.orderType != OrderType.PERSONAL) {
                viewModel.clearData()
                viewModel.orderType = OrderType.PERSONAL
                binding.btnPersonal.setBackgroundResource(R.drawable.selector_bkg_button_gray_rect)
                binding.btnCompany.setBackgroundResource(R.drawable.bkg_button_blue_rect_disabled)
                orderingForm = buildForm(OrderType.PERSONAL)
                orderingAdapter.formFieldList = orderingForm
            }
        }

        binding.btnCompany.setOnClickListener {
            if (viewModel.orderType != OrderType.COMPANY) {
                viewModel.clearData()
                viewModel.orderType = OrderType.COMPANY
                binding.btnCompany.setBackgroundResource(R.drawable.selector_bkg_button_gray_rect)
                binding.btnPersonal.setBackgroundResource(R.drawable.bkg_button_blue_rect_disabled)
                orderingForm = buildForm(OrderType.COMPANY)
                orderingAdapter.formFieldList = orderingForm
            }
        }
    }

    private fun buildForm(orderType: OrderType): List<FormField> {
        val formFieldList = mutableListOf<FormField>()

        formFieldList.add(FormField.SingleLineWithPromptField(
            type = FieldType.ADDRESS,
            name = getString(R.string.ordering_form_address_title_text),
            hint = getString(R.string.ordering_form_address_hint_text),
            prompt = getString(R.string.ordering_form_address_prompt_text),
            isEditable = false
        ))

        if (orderType == OrderType.COMPANY) {
            formFieldList.add(FormField.SingleLineField(
                type = FieldType.COMPANY_NAME,
                name = getString(R.string.ordering_form_company_name_title_text),
                hint = getString(R.string.ordering_form_company_name_hint_text),
                isEditable = true
            ))
        }

        formFieldList.add(FormField.SingleLineField(
            type = FieldType.NAME,
            name = getString(R.string.ordering_form_name_title_text),
            hint = getString(R.string.ordering_form_name_hint_text),
            isEditable = true
        ))

        formFieldList.add(FormField.SingleLineField(
            type = FieldType.EMAIL,
            name = getString(R.string.ordering_form_email_title_text),
            hint = getString(R.string.ordering_form_email_hint_text),
            isEditable = true
        ))

        formFieldList.add(FormField.SingleLineField(
            type = FieldType.PHONE,
            name = getString(R.string.ordering_form_phone_title_text),
            hint = getString(R.string.ordering_form_phone_hint_text),
            isEditable = true
        ))

        formFieldList.add(FormField.DoubleLineField(
            type = FieldType.DATE,
            name = getString(R.string.ordering_form_date_title_text),
            firstHint = getString(R.string.ordering_form_date_hint_text),
            secondHint = getString(R.string.ordering_form_time_hint_text),
            isEditableFirst = false,
            isEditableSecond = false
        ))

        formFieldList.add(FormField.SingleLineField(
            type = FieldType.PAY_METHOD,
            name = getString(R.string.ordering_form_pay_method_title_text),
            hint = getString(R.string.ordering_form_pay_method_hint_text),
            isEditable = false
        ))

        formFieldList.add(FormField.SwitchField(
            type = FieldType.OPERATOR_CALL,
            name = getString(R.string.ordering_form_operator_call_title_text)
        ))

        formFieldList.add(FormField.SwitchField(
            type = FieldType.ALERT_DRIVER,
            name = getString(R.string.ordering_form_alert_driver_title_text)
        ))

        formFieldList.add(FormField.ValueField(
            type = FieldType.COMMENT,
            hint = getString(R.string.ordering_form_comment_hint_text),
            isEditable = true
        ))

        formFieldList.add(FormField.TitleField(
            type = FieldType.TITLE,
            name = getString(R.string.ordering_form_price_title_text)
        ))

        formFieldList.add(FormField.PriceField(
            type = FieldType.PRODUCTS_PRICE,
            name = getString(R.string.ordering_form_products_price_title_text),
            value = 1
        ))

        formFieldList.add(FormField.PriceField(
            type = FieldType.DEPOSIT,
            name = getString(R.string.ordering_form_deposit_title_text),
            value = 2
        ))

        formFieldList.add(FormField.PriceField(
            type = FieldType.DISCOUNT,
            name = getString(R.string.ordering_form_discount_title_text),
            value = 3
        ))

        formFieldList.add(FormField.PriceField(
            type = FieldType.DELIVERY_PRICE,
            name = getString(R.string.ordering_form_delivery_price_title_text),
            value = 4
        ))

        formFieldList.add(FormField.PriceField(
            type = FieldType.TOTAL,
            name = getString(R.string.ordering_form_total_title_text),
            value = 5
        ))

        return formFieldList
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LogSettings.MAP_LOG, "DESTROY")
    }
}

enum class OrderType {
    PERSONAL, COMPANY
}

class FreeShippingSaysBS : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = BsFreeShippingDaysBinding.inflate(
        layoutInflater,
        container,
        false
    ).apply {
        this.incHeader.imgClose.setOnClickListener { dismiss() }
        this.incHeader.tvTitle.text = FreeShippingSaysBSArgs.fromBundle(requireArguments()).title
        this.tvContent.text = HtmlCompat.fromHtml(FreeShippingSaysBSArgs.fromBundle(requireArguments()).info, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }.root

}

class PayMethodSelectionBS : BottomSheetDialogFragment() {

    private val payMethodsAdapter = PayMethodsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = BsSelectionPayMethodBinding.inflate(
        layoutInflater,
        container,
        false
    ).apply {
        this.rvPayMethods.layoutManager = LinearLayoutManager(requireContext())
        payMethodsAdapter.setupListeners {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(OrderingFragment.SELECTED_PAY_METHOD, it.id)
            dismiss()
        }
        PayMethodSelectionBSArgs.fromBundle(requireArguments()).let { args ->
            payMethodsAdapter.updateData(args.payMethodList.toList(), args.selectedPayMethod)
        }

        this.rvPayMethods.adapter = payMethodsAdapter
        val space8 = resources.getDimension(R.dimen.space_8).toInt()
        this.rvPayMethods.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.top = space8
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space8
        }
    }.root

}