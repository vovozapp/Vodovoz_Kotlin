package com.vodovoz.app.feature.all.comments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentProductCommentsBinding
import com.vodovoz.app.databinding.FragmentProductCommentsFlowBinding
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.productlist.ProductsListFlowController
import com.vodovoz.app.ui.fragment.all_comments_by_product.AllCommentsByProductDialogFragmentArgs
import com.vodovoz.app.ui.fragment.all_comments_by_product.AllCommentsByProductDialogFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCommentsFlowFragment : BaseFragment() {

    private val binding: FragmentProductCommentsFlowBinding by viewBinding {
        FragmentProductCommentsFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: AllCommentsFlowViewModel by viewModels()

    private val args: AllCommentsByProductDialogFragmentArgs by navArgs()

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
        when(item.itemId) {
            R.id.sendComment -> {
                if (viewModel.isLoginAlready()) {
                    findNavController().navigate(
                        AllCommentsByProductDialogFragmentDirections.actionToSendCommentAboutProductFragment(
                        args.productId
                    ))
                } else {
                    findNavController().navigate(R.id.profileFragment)
                }
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