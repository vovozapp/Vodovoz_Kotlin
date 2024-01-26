package com.vodovoz.app.feature.profile.discountcard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentDiscountCardBinding
import com.vodovoz.app.feature.profile.discountcard.adapter.DiscountCardAdapter
import com.vodovoz.app.feature.profile.discountcard.adapter.DiscountCardClickListener
import com.vodovoz.app.util.extensions.fromHtml
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DiscountCardFragment : BaseFragment() {

    private val binding: FragmentDiscountCardBinding by viewBinding {
        FragmentDiscountCardBinding.bind(contentView)
    }
    internal val viewModel: DiscountCardFlowViewModel by viewModels()

    private val adapter: DiscountCardAdapter = DiscountCardAdapter(
        object : DiscountCardClickListener {
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeEvent()
                    .collect {
                        when (it) {
                            is DiscountCardFlowViewModel.DiscountCardEvents.ActivateResult -> {
                                requireActivity().snack(it.message)
                            }
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