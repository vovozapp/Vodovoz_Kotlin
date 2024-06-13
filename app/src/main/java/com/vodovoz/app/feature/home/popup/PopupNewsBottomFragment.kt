package com.vodovoz.app.feature.home.popup

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsPopupNewsBinding
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.util.SpanWithUrlHandler

class PopupNewsBottomFragment : BaseBottomSheetFragment() {

    companion object {
        fun newInstance(
            popupNewsUI: PopupNewsUI,
            clickListener: NewsClickListener
        ) = PopupNewsBottomFragment().apply {
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
        binding.incHeader.imgClose.isVisible = false
    }

    private fun initView() {
        binding.incHeader.tvTitle.text = popupNewsUI.name
        val text = popupNewsUI.detailText
        if(text != null) {
            SpanWithUrlHandler.setTextWithUrl(text, binding.tvDetails){ _, _ ->}
        }
//        binding.tvDetails.text = popupNewsUI.detailText?.fromHtml()
        binding.tvDetails.isVisible = !popupNewsUI.detailText.isNullOrEmpty()

        binding.incHeader.imgClose.setOnClickListener { dismiss() }

        this.isCancelable = popupNewsUI.androidVersion.isNullOrEmpty()

        Glide.with(requireContext())
            .load(popupNewsUI.detailPicture)
            .into(binding.imgBanner)

        popupNewsUI.actionEntity?.action?.let{
            binding.btnShowDetails.text = popupNewsUI.actionEntity?.action
            binding.btnShowDetails.isVisible = true
            binding.btnShowDetails.setOnClickListener {
                popupNewsUI.actionEntity?.let { action ->
                    clickListener.onClick(action)
                }
                dismiss()
            }
        } ?: {
            binding.btnShowDetails.isVisible = false
        }

        popupNewsUI.actionEntity?.actionColor?.let {
            val color = Color.parseColor(popupNewsUI.actionEntity!!.actionColor)
            binding.btnShowDetails.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

}
