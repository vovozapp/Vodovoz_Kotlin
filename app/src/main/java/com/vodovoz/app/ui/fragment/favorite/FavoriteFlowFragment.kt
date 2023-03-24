package com.vodovoz.app.ui.fragment.favorite

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.databinding.FragmentMainFavoriteFlowBinding
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentMainFavoriteBinding
import com.vodovoz.app.ui.fragment.favorite.categorytabsdadapter.CategoryTabsFlowClickListener
import com.vodovoz.app.ui.fragment.favorite.categorytabsdadapter.CategoryTabsFlowController
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_main_favorite

    private val binding: FragmentMainFavoriteBinding by viewBinding {
        FragmentMainFavoriteBinding.bind(
            contentView
        )
    }

    private val viewModel: FavoriteFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private val categoryTabsController = CategoryTabsFlowController(categoryTabsClickListener())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
        categoryTabsController.bind(binding.categoriesRecycler, space)
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->

                    bindHeader(state.data)

                }
        }
    }

    private fun bindHeader(state: FavoriteFlowViewModel.FavoriteState) {

        if (state.favoriteCategory != null) {
            showContainer(true)
        }

        if (state.bestForYouCategoryDetailUI != null) {
            showContainer(false)
        }

        binding.tvCategoryName.text = state.favoriteCategory?.name
        binding.tvProductAmount.text = state.favoriteCategory?.productAmount.toString()
        binding.availableTitle.text = state.availableTitle
        binding.notAvailableTitle.text = state.notAvailableTitle
        binding.availableContainer.isVisible = state.availableTitle != null || state.notAvailableTitle != null

    }

    private fun showContainer(bool: Boolean) {
        binding.emptyFavoriteContainer.isVisible = !bool
        binding.favoriteContainer.isVisible = bool
    }

    private fun categoryTabsClickListener() : CategoryTabsFlowClickListener {
        return object : CategoryTabsFlowClickListener {
            override fun onTabClick(id: Long) {
                viewModel.onTabClick(id)
            }
        }
    }

}