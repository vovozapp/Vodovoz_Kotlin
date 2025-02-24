package com.vodovoz.app.feature.categories

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
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.categories.model.CategoriesEvent
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.home.model.PopularCategoryUi

class CategoriesFragment : Fragment() {

    val viewModel by viewModels<CategoriesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categories = arguments?.getParcelableArray("categoryList") as? Array<CategoryUi>
        val currentCategory = arguments?.getParcelable("category") ?: categories?.firstOrNull()

        if (categories != null && currentCategory != null) {
            viewModel.setInitialData(categories.toList(), currentCategory)
        } else {
            findNavController().popBackStack()
        }
    }

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

                    CategoriesScreen(viewModel = viewModel, viewState = viewState)

                    LifecycleEffect {
                        viewModel.events.collect { event ->
                            when (event) {
                                is CategoriesEvent.GoBackWithArguments -> {
                                    val navController = findNavController()
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "category",
                                        event.currentCategory
                                    )
                                    navController.popBackStack()
                                }

                                CategoriesEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}