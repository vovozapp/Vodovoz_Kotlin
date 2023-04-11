package com.vodovoz.app.feature.home.popup

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsPopupNewsBinding
import com.vodovoz.app.ui.model.PopupNewsUI

class PopupNewsFlowBottomFragment : BaseBottomSheetFragment() {

    companion object {
        fun newInstance(
            popupNewsUI: PopupNewsUI,
            clickListener: NewsClickListener
        ) = PopupNewsFlowBottomFragment().apply {
            this.popupNewsUI = popupNewsUI
            this.clickListener = clickListener
        }
    }

    private lateinit var popupNewsUI: PopupNewsUI
    private lateinit var clickListener: NewsClickListener

    override fun layout(): Int = R.layout.bs_popup_news

    private val binding: BsPopupNewsBinding by viewBinding {
        BsPopupNewsBinding.bind(
            contentView
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.incHeader.tvTitle.text = popupNewsUI.name
        binding.tvDetails.text = popupNewsUI.detailText
        binding.btnShowDetails.text = popupNewsUI.actionEntity?.action
        binding.incHeader.imgClose.setOnClickListener { dismiss() }
        Glide.with(requireContext())
            .load(popupNewsUI.detailPicture)
            .into(binding.imgBanner)

        binding.btnShowDetails.setOnClickListener {
            popupNewsUI.actionEntity?.let { action ->
                clickListener.onClick(action)
            }
            dismiss()
        }
    }

}