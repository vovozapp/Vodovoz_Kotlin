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
        fun newInstance(title: String, content: String, rating: Int, date: String): HomeCommentsFullBottomSheetFragment {
            return HomeCommentsFullBottomSheetFragment().apply {
                arguments = bundleOf("title" to title, "content" to content, "rating" to rating, "date" to date)
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
            val name = requireArguments().getString("title")

            binding.tvTitle.text = if (name.isNullOrEmpty().not()) {
                name
            } else {
                "Анонимно"
            }
            binding.tvContent.text = requireArguments().getString("content")
            binding.rbRating.rating = requireArguments().getInt("rating").toFloat()
            binding.tvDate.text = requireArguments().getString("date")
        }
    }
}