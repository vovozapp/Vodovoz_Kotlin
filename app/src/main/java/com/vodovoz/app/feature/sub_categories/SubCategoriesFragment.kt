package com.vodovoz.app.feature.sub_categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.core.android.activate
import com.vodovoz.app.core.navigation.navigateToCategoryProductList
import com.vodovoz.app.core.navigation.navigateToSearch
import com.vodovoz.app.core.navigation.navigateToSubCategories
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.sub_categories.model.SubCategoriesEvent
import com.vodovoz.app.util.extensions.debugLog

class SubCategoriesFragment : Fragment() {

    val viewModel by viewModels<SubCategoriesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)

            setContent {
                VodovozTheme {
                    val viewState by viewModel.state.collectAsStateWithLifecycle()

                    SubCategoriesScreen(
                        viewModel = viewModel,
                        viewState = viewState
                    )

                    LifecycleEffect {
                        viewModel.events.collect { event ->
                            when (event) {
                                is SubCategoriesEvent.GoToProductList -> {
                                    findNavController().navigateToCategoryProductList(event.categoryId)
                                }

                                is SubCategoriesEvent.GoToSubCategories -> {
                                    findNavController().navigateToSubCategories(event.category)
                                }

                                SubCategoriesEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                SubCategoriesEvent.GoToSearch -> {
                                    findNavController().navigateToSearch()
                                }

                                is SubCategoriesEvent.ActivateDataAllAction -> {
                                    event.action.activate(findNavController())
                                }
                            }

                        }
                    }
                }
            }

        }
    }

}