package com.vodovoz.app.feature.all.comments

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.vodovoz.app.R
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.feature.productdetail.viewholders.detailcomments.inner.CommentsWithAvatarFlowAdapter

class AllCommentsController(
    private val viewModel: AllCommentsFlowViewModel,
    private val context: Context,
) : ItemController(CommentsWithAvatarFlowAdapter()) {

    internal val space: Int by lazy { context.resources.getDimension(R.dimen.space_16).toInt() }

    override fun bindRefresh(refresh: SwipeRefreshLayout) {
        refresh.setOnRefreshListener {
            viewModel.refreshSorted()
            refresh.isRefreshing = false
        }
    }

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State,
                    ) {
                        with(outRect) {
                            val space =
                                context.resources.getDimension(R.dimen.space_8).toInt()
                            left = space
                            right = space
                            if (parent.getChildAdapterPosition(view) != 0) {
                                top = space / 2
                            } else {
                                top = space
                            }
                            if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
                                bottom = space / 2
                            } else {
                                bottom = space
                            }
                        }
                    }
                }
            )

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        viewModel.loadMoreSorted()
                    }
                }
            })
        }
    }
}