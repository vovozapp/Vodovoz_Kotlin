package com.vodovoz.app.feature.all.comments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentProductCommentsFlowBinding

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllCommentsByProductDialogFragment : BaseFragment() {

    private val binding: FragmentProductCommentsFlowBinding by viewBinding {
        FragmentProductCommentsFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: AllCommentsFlowViewModel by viewModels()

    private val args: AllCommentsByProductDialogFragmentArgs by navArgs()

    @Inject
    lateinit var tabManager: TabManager

    override fun layout(): Int = R.layout.fragment_product_comments_flow

    private val allCommentsFlowController by lazy {
        AllCommentsController(viewModel, requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.firstLoadSorted()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.all_comments_by_product_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sendComment -> {
                viewModel.onSendCommentClick()
            }
        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionBar()
        allCommentsFlowController.bind(binding.rvComments, binding.refreshContainer)
        bindErrorRefresh {
            viewModel.refreshSorted()
        }
        observeUiState()
        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {
                    when(it) {
                        is AllCommentsFlowViewModel.AllCommentsEvents.GoToProfile -> {
                            tabManager.selectTab(R.id.graph_profile)
                        }
                        is AllCommentsFlowViewModel.AllCommentsEvents.SendComment -> {
                            findNavController().navigate(
                                AllCommentsByProductDialogFragmentDirections.actionToSendCommentAboutProductFragment(
                                    args.productId
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->

                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                        binding.apAppBar.elevation = 4F
                    }

                    val data = state.data
                    if (state.bottomItem != null) {
                        allCommentsFlowController.submitList(data.itemsList + state.bottomItem)
                    } else {
                        allCommentsFlowController.submitList(data.itemsList)
                    }

                    showError(state.error)

                }
        }
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

}
/*
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

}*/
