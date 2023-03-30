package com.vodovoz.app.ui.fragment.all_comments_by_product

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentProductCommentsBinding
import com.vodovoz.app.feature.productdetail.ProductDetailsFragmentDirections
import com.vodovoz.app.ui.adapter.PagingCommentsAdapter
import com.vodovoz.app.ui.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.base.loadStateAdapter.LoadStateAdapter
import com.vodovoz.app.ui.diffUtils.CommentDiffItemCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation

import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllCommentsByProductDialogFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: FragmentProductCommentsBinding
    private val viewModel: AllCommentsByProductViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()
    private val onUpdateSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val pagingCommentsAdapter = PagingCommentsAdapter(CommentDiffItemCallback())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        getArgs()
        subscribeSubjects()
    }

    private fun getArgs() {
        viewModel.updateArgs(AllCommentsByProductDialogFragmentArgs.fromBundle(requireArguments()).productId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.all_comments_by_product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sendComment -> {
                if (viewModel.isLogin()) {
                    findNavController().navigate(ProductDetailsFragmentDirections.actionToSendCommentAboutProductFragment(
                        viewModel.productId!!
                    ))
                } else {
                    findNavController().navigate(R.id.profileFragment)
                }
            }
        }
        return false
    }

    private fun subscribeSubjects() {
        onUpdateSubject.subscribeBy {
            pagingCommentsAdapter.refresh()
        }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentProductCommentsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initActionBar()
        initCommentsRecycler()
        observeViewModel()
    }

    private fun initActionBar() {
        (requireActivity() as AppCompatActivity).let { appCompatActivity ->
            appCompatActivity.setSupportActionBar(binding.tbToolbar)
            appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun initCommentsRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.rvComments.adapter = pagingCommentsAdapter
        binding.rvComments.setScrollElevation(binding.apAppBar)
        binding.rvComments.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        left = space
                        top = space
                        bottom = space
                        right = space
                    }
                }
            }
        )

        pagingCommentsAdapter.addLoadStateListener { state ->
            when (state.refresh) {
                is LoadState.Loading -> {}
                is LoadState.Error -> onStateError((state.refresh as LoadState.Error).error.message)
                is LoadState.NotLoading -> onStateSuccess()
            }
        }

        binding.rvComments.adapter = pagingCommentsAdapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(onUpdateSubject),
            footer = LoadStateAdapter(onUpdateSubject)
        )
    }

    override fun update() {}

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateData().collectLatest { commentUIList ->
                pagingCommentsAdapter.submitData(commentUIList)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}