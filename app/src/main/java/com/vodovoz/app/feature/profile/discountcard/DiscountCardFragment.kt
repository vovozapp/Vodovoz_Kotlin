package com.vodovoz.app.feature.profile.discountcard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentDiscountCardBinding
import com.vodovoz.app.feature.profile.discountcard.DiscountCardFlowViewModel
import com.vodovoz.app.feature.profile.discountcard.adapter.DiscountCardAdapter
import com.vodovoz.app.feature.profile.discountcard.adapter.DiscountCardClickListener
import com.vodovoz.app.ui.adapter.DiscountCardPropertiesAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.util.extensions.fromHtml
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscountCardFragment : BaseFragment() {

    private val binding: FragmentDiscountCardBinding by viewBinding {
        FragmentDiscountCardBinding.bind(contentView)
    }
    private val viewModel: DiscountCardFlowViewModel by viewModels()

    private val adapter: DiscountCardAdapter = DiscountCardAdapter(object : DiscountCardClickListener {
        override fun onCardValueChange(value: String) {
            viewModel.changeCardValue(value)
        }
    })

    override fun layout(): Int = R.layout.fragment_discount_card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar("Активация скидочной карты")
        initDiscountCardPropertiesRecycler()
        initButtons()
        observeUiState()
        observeEvents()
    }

    private fun initDiscountCardPropertiesRecycler() {
        binding.discountCardPropertiesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.discountCardPropertiesRecycler.adapter = adapter
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    val bundle = state.data.activateDiscountCardBundleUI
                    if (bundle != null) {
                        initToolbar(bundle.title)
                        binding.info.text = bundle.details.fromHtml()
                        adapter.submitList(bundle.discountCardPropertyUIList)
                    }

                    showError(state.error)
                }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeEvent()
                .collect {
                    when(it) {
                        is DiscountCardFlowViewModel.DiscountCardEvents.ActivateResult -> {
                            requireActivity().snack(it.message)
                        }
                    }
                }
        }
    }

    private fun initButtons() {
        binding.submit.setOnClickListener {
            viewModel.activateDiscountCard()
        }
    }
}
/*
@AndroidEntryPoint
class DiscountCardFragment : BaseFragment() {

    private val binding: FragmentDiscountCardBinding by viewBinding {
        FragmentDiscountCardBinding.bind(contentView)
    }
    private val viewModel: DiscountCardViewModel by viewModels()

    private val discountCardPropertiesAdapter = DiscountCardPropertiesAdapter()

    override fun layout(): Int = R.layout.fragment_discount_card

    override fun update() {
        viewModel.fetchData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        update()
    }

    override fun initView() {
        initAppBar()
        initDiscountCardPropertiesRecycler()
        initButtons()
        observeViewModel()
    }

    private fun initAppBar() {
        initToolbar("Активация скидочной карты")
    }

    private fun initDiscountCardPropertiesRecycler() {
        binding.discountCardPropertiesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.discountCardPropertiesRecycler.adapter = discountCardPropertiesAdapter
    }

    private fun initButtons() {
        binding.submit.setOnClickListener {
            viewModel.activateDiscountCard()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {

        viewModel.activateDiscountCardBundleUILD.observe(viewLifecycleOwner) { activateDiscountCardBundleUI ->
            binding.info.text = activateDiscountCardBundleUI.details.fromHtml()
            initToolbar(activateDiscountCardBundleUI.title)

            discountCardPropertiesAdapter.discountCardPropertyUIList = activateDiscountCardBundleUI.discountCardPropertyUIList
            discountCardPropertiesAdapter.notifyDataSetChanged()
        }

        viewModel.messageLD.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

}*/
