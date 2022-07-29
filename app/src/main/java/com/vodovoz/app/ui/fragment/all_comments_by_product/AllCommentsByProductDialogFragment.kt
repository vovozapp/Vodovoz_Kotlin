package com.vodovoz.app.ui.fragment.all_comments_by_product

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.DialogFragmentAllProductCommentsBinding
import com.vodovoz.app.ui.adapter.PagingCommentsAdapter
import com.vodovoz.app.ui.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.base.loadStateAdapter.LoadStateAdapter
import com.vodovoz.app.ui.diffUtils.CommentDiffItemCallback
import com.vodovoz.app.ui.fragment.product_detail.ProductDetailFragmentDirections
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AllCommentsByProductDialogFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: DialogFragmentAllProductCommentsBinding
    private lateinit var viewModel: AllCommentsByProductViewModel

    private val compositeDisposable = CompositeDisposable()
    private val onUpdateSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val pagingCommentsAdapter = PagingCommentsAdapter(CommentDiffItemCallback())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        initViewModel()
        getArgs()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AllCommentsByProductViewModel::class.java]
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
                    findNavController().navigate(ProductDetailFragmentDirections.actionToSendCommentAboutProductFragment(
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
    ) = DialogFragmentAllProductCommentsBinding.inflate(
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
            appCompatActivity.setSupportActionBar(binding.toolbar)
            appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun initCommentsRecycler() {
        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.commentsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.commentsRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.commentsRecycler.adapter = pagingCommentsAdapter
        binding.commentsRecycler.setScrollElevation(binding.appBar)
        binding.commentsRecycler.addItemDecoration(
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

        binding.commentsRecycler.adapter = pagingCommentsAdapter.withLoadStateHeaderAndFooter(
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
        compositeDisposable.clear()
    }

}