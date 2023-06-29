package com.vodovoz.app.feature.home.viewholders.homecomments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsHomeFullCommentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeCommentsFullBottomSheetFragment : BaseBottomSheetFragment() {

    companion object {
        fun newInstance(title: String, content: String): HomeCommentsFullBottomSheetFragment {
            return HomeCommentsFullBottomSheetFragment().apply {
                arguments = bundleOf("title" to title, "content" to content)
            }
        }
    }

    override fun layout(): Int {
        return R.layout.bs_home_full_comment
    }

    private val binding: BsHomeFullCommentBinding by viewBinding {
        BsHomeFullCommentBinding.bind(contentView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgClose.setOnClickListener { dismiss() }
        if (arguments != null) {
            binding.tvTitle.text = requireArguments().getString("title")
            binding.tvContent.text = requireArguments().getString("content")
        }
    }
}