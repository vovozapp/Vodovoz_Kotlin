package com.vodovoz.app.ui.components.fragment.popup_news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.BottomFragmentPopupNewsBinding
import com.vodovoz.app.ui.model.PopupNewsUI

class PopupNewsBottomFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            popupNewsUI: PopupNewsUI,
            iOnInvokeAction: IOnInvokeAction
        ) = PopupNewsBottomFragment().apply {
            this.popupNewsUI = popupNewsUI
            this.iOnInvokeAction = iOnInvokeAction
        }
    }

    private lateinit var popupNewsUI: PopupNewsUI
    private lateinit var iOnInvokeAction: IOnInvokeAction

    private lateinit var binding: BottomFragmentPopupNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomFragmentPopupNewsBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initView()
        initDialog()
    }.root

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initView() {
        binding.name.text = popupNewsUI.name
        binding.detailText.text = popupNewsUI.name
        Glide.with(requireContext())
            .load(popupNewsUI.detailPicture)
            .into(binding.detailPicture)

        binding.action.setOnClickListener {
            popupNewsUI.actionEntity?.let { action ->
                iOnInvokeAction.invokeAction(action)
            }
        }

    }


    fun interface IOnInvokeAction {
        fun invokeAction(actionEntity: ActionEntity)
    }
}