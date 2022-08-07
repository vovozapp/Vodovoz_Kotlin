package com.vodovoz.app.ui.fragment.ordering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentOrderingBinding
import com.vodovoz.app.ui.adapter.FieldType
import com.vodovoz.app.ui.adapter.FormAdapter
import com.vodovoz.app.ui.adapter.FormField
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation

class OrderingFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentOrderingBinding
    private lateinit var viewModel: OrderingViewModel

    private val orderingAdapter = FormAdapter()

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
        container: ViewGroup
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
        orderingAdapter.formFieldList = buildForm()
    }

    private fun setupActionBar() {
        (requireActivity() as AppCompatActivity).let { appCompatActivity ->
            appCompatActivity.setSupportActionBar(binding.tb)
            appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tb.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.nsvContainer.setScrollElevation(binding.ab)
    }

    private fun setupOrderingRecycler() {
        binding.rvOrdering.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrdering.adapter = orderingAdapter
        orderingAdapter.setupListeners(
            afterErrorChange = {

            },
            onPromptClick = {

            }
        )
    }

    private fun setupButtons() {
        binding.btnPersonal.setOnClickListener {
            if (viewModel.orderType != OrderType.PERSONAL) {
                viewModel.orderType = OrderType.PERSONAL
                binding.btnPersonal.setBackgroundResource(R.drawable.bkg_button_green_circle_disabled)
                binding.btnCompany.setBackgroundResource(R.drawable.bkg_button_blue_rect_disabled)
                orderingAdapter.formFieldList = buildForm()
            }
        }

        binding.btnCompany.setOnClickListener {
            if (viewModel.orderType != OrderType.COMPANY) {
                viewModel.orderType = OrderType.COMPANY
                binding.btnCompany.setBackgroundResource(R.drawable.bkg_button_green_circle_disabled)
                binding.btnPersonal.setBackgroundResource(R.drawable.bkg_button_blue_rect_disabled)
                orderingAdapter.formFieldList = buildForm()
            }
        }
    }

    private fun buildForm(): List<FormField> {
        val formFieldList = mutableListOf<FormField>()

        formFieldList.add(FormField.SingleLineWithPromptField(
            type = FieldType.ADDRESS,
            name = getString(R.string.ordering_form_address_title_text),
            hint = getString(R.string.ordering_form_address_hint_text),
            prompt = getString(R.string.ordering_form_address_prompt_text)
        ))

        if (viewModel.orderType == OrderType.COMPANY) {
            formFieldList.add(FormField.SingleLineField(
                type = FieldType.COMPANY_NAME,
                name = getString(R.string.ordering_form_company_name_title_text),
                hint = getString(R.string.ordering_form_company_name_hint_text)
            ))
        }

        formFieldList.add(FormField.SingleLineField(
            type = FieldType.NAME,
            name = getString(R.string.ordering_form_name_title_text),
            hint = getString(R.string.ordering_form_name_hint_text)
        ))

        formFieldList.add(FormField.SingleLineField(
            type = FieldType.EMAIL,
            name = getString(R.string.ordering_form_email_title_text),
            hint = getString(R.string.ordering_form_email_hint_text)
        ))

        formFieldList.add(FormField.SingleLineField(
            type = FieldType.PHONE,
            name = getString(R.string.ordering_form_phone_title_text),
            hint = getString(R.string.ordering_form_phone_hint_text)
        ))

        formFieldList.add(FormField.DoubleLineField(
            type = FieldType.DATE,
            name = getString(R.string.ordering_form_date_title_text),
            firstHint = getString(R.string.ordering_form_date_hint_text),
            secondHint = getString(R.string.ordering_form_time_hint_text)
        ))

        formFieldList.add(FormField.SingleLineField(
            type = FieldType.PAY_METHOD,
            name = getString(R.string.ordering_form_pay_method_title_text),
            hint = getString(R.string.ordering_form_pay_method_hint_text)
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
            hint = getString(R.string.ordering_form_comment_hint_text)
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

}

enum class OrderType {
    PERSONAL, COMPANY
}