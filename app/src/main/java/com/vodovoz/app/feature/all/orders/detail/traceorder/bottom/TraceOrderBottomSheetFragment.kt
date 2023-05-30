package com.vodovoz.app.feature.all.orders.detail.traceorder.bottom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.FragmentTraceOrderBottomBinding
import com.vodovoz.app.feature.all.orders.detail.traceorder.TraceOrderViewModel
import com.vodovoz.app.feature.bottom.contacts.ContactsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TraceOrderBottomSheetFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.fragment_trace_order_bottom

    private val binding: FragmentTraceOrderBottomBinding by viewBinding {
        FragmentTraceOrderBottomBinding.bind(contentView)
    }

    private val viewModel: TraceOrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
        bindButtons()
        initBottomSheetCallback()
    }

    private fun initBottomSheetCallback() {
        val behavior = (dialog as? BottomSheetDialog)?.behavior
        val density = requireContext().resources.displayMetrics.density
        behavior?.peekHeight = (160 * density).toInt()
        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior?.isHideable = false
    }

    private fun bindButtons() {
        binding.callUsBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+74959213434", null))
            startActivity(intent)
        }

        binding.chatUsBtn.setOnClickListener {
            findNavController().navigate(
                TraceOrderBottomSheetFragmentDirections.actionToWebViewFragment(
                "http://jivo.chat/mk31km1IlP",
                "Чат"
            ))
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect {
                    binding.driverNameTv.text = it.data.name ?: ""
                    binding.carNumberTv.text = it.data.car ?: ""

                    if (it.data.driverPointsEntity != null) {
                        if (it.data.driverPointsEntity.DriverDirection == "TRUE") {
                            binding.timeTv.isVisible = false
                            binding.commentTv.isVisible = true
                            binding.commentTv.text = "Водитель выехал и направляется к Вам."
                        } else {
                            binding.timeTv.isVisible = true
                            binding.commentTv.isVisible = false
                            binding.commentTv.text = "Ориентировочное время прибытия: ${it.data.driverPointsEntity.Priblizitelnoe_vremya}"
                        }
                    } else {
                        binding.timeTv.isVisible = false
                        binding.commentTv.isVisible = false
                    }
                }
        }
    }

}