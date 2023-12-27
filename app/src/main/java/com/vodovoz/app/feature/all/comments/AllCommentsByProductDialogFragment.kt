package com.vodovoz.app.feature.all.comments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentProductCommentsFlowBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect {
                        when (it) {
                            is AllCommentsFlowViewModel.AllCommentsEvents.GoToProfile -> {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_profile)
                            }
                            is AllCommentsFlowViewModel.AllCommentsEvents.SendComment -> {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.sendCommentAboutProductFragment) {
                                    findNavController().popBackStack()
                                }
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
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
