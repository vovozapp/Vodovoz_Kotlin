package com.vodovoz.app.feature.categories

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.feature.categories.model.CategoriesEvent
import com.vodovoz.app.feature.categories.model.CategoriesState
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.home.model.PopularCategoryUi
import com.vodovoz.app.ui.mvi.MviViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel : MviViewModel<CategoriesState, CategoriesEvent>(CategoriesState()) {

    fun selectCategory(category: CategoryUi) = viewModelScope.launch {
        _state.update { s ->
            s.copy(currentCategory = category)
        }
    }

    fun setInitialData(categories: List<CategoryUi>, currentCategory: CategoryUi) =
        viewModelScope.launch {
            _state.update { s ->
                s.copy(
                    currentCategory = currentCategory,
                    categories = categories
                )
            }
        }

    fun navigateBackWithArgs() = viewModelScope.launch {
        _events.emit(CategoriesEvent.GoBackWithArguments(stateSnapshot.currentCategory))
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(CategoriesEvent.GoBack)
    }


}