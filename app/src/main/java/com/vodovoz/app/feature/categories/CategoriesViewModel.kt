package com.vodovoz.app.feature.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.feature.categories.model.CategoriesEvent
import com.vodovoz.app.feature.categories.model.CategoriesState
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.ui.mvi.MviViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel(
    savedStateHandle: SavedStateHandle,
) : MviViewModel<CategoriesState, CategoriesEvent>(CategoriesState()) {

    private val categoriesArg =
        savedStateHandle.get<Array<CategoryUi>>("categoryList") ?: emptyArray()
    private val categoryArg =
        savedStateHandle.get<CategoryUi>("category") ?: categoriesArg.firstOrNull()
        ?: CategoryUi.Empty

    init {
        setInitialCategories()
    }


    private fun setInitialCategories() = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                currentCategory = categoryArg,
                categories = categoriesArg.toList()
            )
        }
    }


    fun selectCategory(category: CategoryUi) = viewModelScope.launch {
        _state.update { s -> s.copy(currentCategory = category) }
    }

    fun navigateBackWithArgs() = viewModelScope.launch {
        _events.emit(CategoriesEvent.GoBackWithArguments(stateSnapshot.currentCategory))
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(CategoriesEvent.GoBack)
    }


}