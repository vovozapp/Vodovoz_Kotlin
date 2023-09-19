package com.vodovoz.app.feature.cart.ordering.intervals

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsCheckDeliveryActionsBinding
import com.vodovoz.app.feature.cart.ordering.OrderingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class CheckDeliveryActionsBS : BaseBottomSheetFragment() {

    override fun layout(): Int {
        return R.layout.bs_check_delivery_actions
    }

    private val binding: BsCheckDeliveryActionsBinding by viewBinding {
        BsCheckDeliveryActionsBinding.bind(contentView)
    }

    private val args: CheckDeliveryActionsBSArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUiFromArgs(args.deliveryAction)

        binding.callMeChangeLL.setOnClickListener {
            navigateToOrder(1, "Позвоните мне. Если не отвечу, замените")
            dismiss()
        }

        binding.callMeDontChangeLL.setOnClickListener {
            navigateToOrder(2, "Позвоните мне. Если не отвечу, уберите")
            dismiss()
        }

        binding.dontCallMeChangeLL.setOnClickListener {
            navigateToOrder(3, "Не звоните. Замените на аналогичный")
            dismiss()
        }

        binding.dontCallMeDontChangeLL.setOnClickListener {
            navigateToOrder(4, "Не звоните. Уберите из заказа")
            dismiss()
        }

        binding.callMeNewLL.setOnClickListener {
            navigateToOrder(5, "Позвоните мне для подтверждения заказа")
            dismiss()
        }

        binding.cancelWarnBs.setOnClickListener {
            dismiss()
        }

    }

    private fun updateUiFromArgs(value: Int?) {
        when(value) {
            0 -> {
                binding.callMeChangeLL.alpha = 1f
                binding.callMeDontChangeLL.alpha = 1f
                binding.dontCallMeChangeLL.alpha = 1f
                binding.dontCallMeDontChangeLL.alpha = 1f
            }
            1 -> {
                binding.callMeChangeLL.alpha = 1f
                binding.callMeDontChangeLL.alpha = 0.4f
                binding.dontCallMeChangeLL.alpha = 0.4f
                binding.dontCallMeDontChangeLL.alpha = 0.4f
            }
            2 -> {
                binding.callMeChangeLL.alpha = 0.4f
                binding.callMeDontChangeLL.alpha = 1f
                binding.dontCallMeChangeLL.alpha = 0.4f
                binding.dontCallMeDontChangeLL.alpha = 0.4f
            }
            3 -> {
                binding.callMeChangeLL.alpha = 0.4f
                binding.callMeDontChangeLL.alpha = 0.4f
                binding.dontCallMeChangeLL.alpha = 1f
                binding.dontCallMeDontChangeLL.alpha = 0.4f
            }
            4 -> {
                binding.callMeChangeLL.alpha = 0.4f
                binding.callMeDontChangeLL.alpha = 0.4f
                binding.dontCallMeChangeLL.alpha = 0.4f
                binding.dontCallMeDontChangeLL.alpha = 1f
            }
            else -> {
                binding.callMeChangeLL.alpha = 1f
                binding.callMeDontChangeLL.alpha = 1f
                binding.dontCallMeChangeLL.alpha = 1f
                binding.dontCallMeDontChangeLL.alpha = 1f
            }
        }
    }

    private fun navigateToOrder(value: Int, text: String) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(OrderingFragment.SELECTED_CHECK_DELIVERY_ACTION, CheckDeliveryUI(value, text))
    }

}

@Parcelize
data class CheckDeliveryUI(
    val value: Int,
    val text: String
) : Parcelable