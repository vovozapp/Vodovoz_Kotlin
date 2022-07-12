package com.vodovoz.app.ui.components.fragment.slider.comments_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.databinding.FragmentSliderCommentBinding
import com.vodovoz.app.ui.components.adapter.CommentsSliderAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.diffUtils.CommentSliderDiffUtilCallback
import com.vodovoz.app.ui.components.interfaces.IOnCommentClick
import com.vodovoz.app.ui.components.interfaces.IOnSendCommentAboutShop
import com.vodovoz.app.ui.model.CommentUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class CommentsSliderFragment : BaseHiddenFragment() {

    private lateinit var commentUIList: List<CommentUI>
    private lateinit var iOnCommentClick: IOnCommentClick
    private lateinit var iOnSendCommentAboutShop: IOnSendCommentAboutShop

    private lateinit var binding: FragmentSliderCommentBinding

    private val compositeDisposable = CompositeDisposable()
    private val onCommentClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val commentsSliderAdapter = CommentsSliderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeSubjects()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderCommentBinding.inflate(
        inflater, container, false
    ).apply { binding = this }.root

    override fun initView() {
        initCommentsPager()
        initButtons()
    }

    private fun initButtons() {
        binding.sendComment.setOnClickListener { iOnSendCommentAboutShop.sendCommentAboutShop() }
    }

    private fun initCommentsPager() {
        binding.commentsPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.commentsPager.adapter = commentsSliderAdapter
    }

    private fun subscribeSubjects() {
        onCommentClickSubject.subscribeBy { commentId ->
            iOnCommentClick.onCommentClick(commentId)
        }.addTo(compositeDisposable)
    }

    fun initCallbacks(
        iOnCommentClick: IOnCommentClick,
        iOnSendCommentAboutShop: IOnSendCommentAboutShop
    ) {
        this.iOnCommentClick = iOnCommentClick
        this.iOnSendCommentAboutShop = iOnSendCommentAboutShop
    }

    fun updateData(commentUIList: List<CommentUI>) {
        this.commentUIList = commentUIList

        val diffUtil = CommentSliderDiffUtilCallback(
            oldList = commentsSliderAdapter.commentUIList,
            newList = commentUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            commentsSliderAdapter.commentUIList = commentUIList
            diffResult.dispatchUpdatesTo(commentsSliderAdapter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}