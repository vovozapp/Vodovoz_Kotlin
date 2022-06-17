package com.vodovoz.app.ui.components.fragment.commentSlider

import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.databinding.FragmentSliderCommentBinding
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.adapter.commentSliderAdapter.CommentSliderAdapter
import com.vodovoz.app.ui.components.adapter.commentSliderAdapter.CommentSliderDiffUtilCallback


class CommentSliderFragment : BaseHiddenFragment<CommentSliderViewModel>() {

    private lateinit var contentBinding: FragmentSliderCommentBinding
    private val commentSliderAdapter = CommentSliderAdapter()

    override fun setContentView() = FragmentSliderCommentBinding.inflate(
        LayoutInflater.from(requireContext())
    ).apply { contentBinding = this }.root

    override fun getViewModelClass() = CommentSliderViewModel::class.java

    override fun initView() {
        initCommentsPager()
        observeViewModel()
    }

    private fun initCommentsPager() {
        contentBinding.commentsPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        contentBinding.commentsPager.adapter = commentSliderAdapter
    }

    private fun observeViewModel() {
        viewModel().sliderCommentListLD.observe(viewLifecycleOwner) { sliderCommentUIList ->
            val diffUtil = CommentSliderDiffUtilCallback(
                oldList = commentSliderAdapter.commentUIList,
                newList = sliderCommentUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                commentSliderAdapter.commentUIList = sliderCommentUIList
                diffResult.dispatchUpdatesTo(commentSliderAdapter)
            }
        }

        viewModel().sateLD.observe(viewLifecycleOwner) { state -> setState(state) }
    }

}