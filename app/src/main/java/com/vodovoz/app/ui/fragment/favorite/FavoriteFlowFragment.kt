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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_main_favorite_flow

    private val binding: FragmentMainFavoriteFlowBinding by viewBinding {
        FragmentMainFavoriteFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: FavoriteFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->

                    if (state.data.favoriteCategory != null) {
                        showContainer(true)
                    }

                    if (state.data.bestForYouCategoryDetailUI != null) {
                        showContainer(false)
                    }

                    binding.availableTitle.text = state.data.availableTitle
                    binding.notAvailableTitle.text = state.data.notAvailableTitle
                    binding.availableContainer.isVisible = state.data.availableTitle != null || state.data.notAvailableTitle != null
                }
        }
    }

    private fun showContainer(bool: Boolean) {
        binding.emptyFavoriteContainer.isVisible = !bool
        binding.favoriteContainer.isVisible = bool
    }

}