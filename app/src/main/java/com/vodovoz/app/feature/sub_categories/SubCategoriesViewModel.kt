package com.vodovoz.app.feature.sub_categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi
import com.vodovoz.app.feature.sub_categories.model.SubCategoriesEvent
import com.vodovoz.app.feature.sub_categories.model.SubCategoriesState
import com.vodovoz.app.ui.mvi.MviViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubCategoriesViewModel(
    savedStateHandle: SavedStateHandle,
) : MviViewModel<SubCategoriesState, SubCategoriesEvent>(SubCategoriesState()) {

    private val catalogCategoryArg =
        savedStateHandle.get<CatalogCategoryUi>("category") ?: CatalogCategoryUi.Empty

    init {
        setInitialCatalogCategory()
    }

    private fun setInitialCatalogCategory() {
        _state.update { s ->
            s.copy(catalogCategory = catalogCategoryArg)
        }
    }

    fun chooseCatalogCategory(catalogCategory: CatalogCategoryUi) = viewModelScope.launch {
        if (catalogCategory.childCategories.isEmpty()) {
            _events.emit(SubCategoriesEvent.GoToProductList(catalogCategory.id))
        } else {
            _events.emit(SubCategoriesEvent.GoToSubCategories(catalogCategory))
        }
    }

    fun navigateToSearch() = viewModelScope.launch {
        _events.emit(SubCategoriesEvent.GoToSearch)
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(SubCategoriesEvent.GoBack)
    }

    fun chooseParentCatalogCategory(catalogCategory: CatalogCategoryUi) = viewModelScope.launch {
        _events.emit(SubCategoriesEvent.GoToProductList(catalogCategory.id))
    }


}