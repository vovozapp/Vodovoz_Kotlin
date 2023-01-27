package com.vodovoz.app.ui.fragment.slider.comments_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderCommentBinding
import com.vodovoz.app.ui.adapter.CommentsSliderAdapter
import com.vodovoz.app.ui.base.BaseHiddenFragment
import com.vodovoz.app.ui.diffUtils.CommentSliderDiffUtilCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.interfaces.IOnCommentClick
import com.vodovoz.app.ui.interfaces.IOnSendCommentAboutShop
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
        binding.tvSendComment.setOnClickListener { iOnSendCommentAboutShop.sendCommentAboutShop() }
    }

    private fun initCommentsPager() {
        binding.vpComments.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpComments.adapter = commentsSliderAdapter
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.vpComments.addMarginDecoration {  rect, view, parent, state ->
            rect.left = space
            rect.right = space
            rect.bottom = space/2
            rect.top = space/2
        }
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
        compositeDisposable.dispose()
    }

}