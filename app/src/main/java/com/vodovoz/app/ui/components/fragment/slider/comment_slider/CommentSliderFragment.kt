package com.vodovoz.app.ui.components.fragment.slider.comment_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.databinding.FragmentSliderCommentBinding
import com.vodovoz.app.ui.components.adapter.CommentsSliderAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.CommentSliderDiffUtilCallback
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject


class CommentSliderFragment : BaseHiddenFragment() {

    companion object {
        fun newInstance(
            onCommentClickSubject: PublishSubject<Long>,
            viewStateSubject: PublishSubject<ViewState>? = null,
            onUpdateSubject: PublishSubject<Boolean>? = null
        ) = CommentSliderFragment().apply {
            this.viewStateSubject = viewStateSubject
            this.onUpdateSubject = onUpdateSubject
            this.onCommentClickSubject = onCommentClickSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var onCommentClickSubject: PublishSubject<Long>
    private var viewStateSubject: PublishSubject<ViewState>? = null
    private var onUpdateSubject: PublishSubject<Boolean>? = null

    private lateinit var binding: FragmentSliderCommentBinding
    private lateinit var viewModel: CommentSliderViewModel

    private val commentsSliderAdapter = CommentsSliderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[CommentSliderViewModel::class.java]
        viewModel.updateData()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderCommentBinding.inflate(
        inflater, container, false
    ).apply { binding = this }.root


    override fun initView() {
        initCommentsPager()
        observeViewModel()
    }

    private fun initCommentsPager() {
        binding.commentsPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.commentsPager.adapter = commentsSliderAdapter
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> {
                    viewStateSubject?.onNext(state)
                    hide()
                }
                else -> viewStateSubject?.onNext(state)
            }
        }

        viewModel.commentUIListLD.observe(viewLifecycleOwner) { commentUIList ->
            val diffUtil = CommentSliderDiffUtilCallback(
                oldList = commentsSliderAdapter.commentUIList,
                newList = commentUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                commentsSliderAdapter.commentUIList = commentUIList
                diffResult.dispatchUpdatesTo(commentsSliderAdapter)
            }
        }
    }

}