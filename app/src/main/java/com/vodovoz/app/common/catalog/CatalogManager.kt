package com.vodovoz.app.common.catalog

import com.vodovoz.app.ui.model.CategoryUI
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogManager @Inject constructor() {

    private val catalogStateListener = MutableStateFlow<List<CategoryUI>>(emptyList())

    fun saveCatalog(catalog: List<CategoryUI>) {
        catalogStateListener.value = catalog
    }

    fun hasRootItems(selectedCategoryId: Long) : Boolean {
        val cat = getRoot(selectedCategoryId) ?: return false
        return cat.categoryUIList.isNotEmpty()
    }

    private fun getRoot(selectedCategoryId: Long): CategoryUI? {
        for (category in catalogStateListener.value) {
            if (category.id == selectedCategoryId) return category
            if (findRoot(category.categoryUIList, selectedCategoryId)) return category
        }
        return null
    }

    private fun findRoot(catalog: List<CategoryUI>, selectedCategoryId: Long): Boolean  {
        for (category in catalog) {
            if (category.id == selectedCategoryId) return true
            if (findRoot(category.categoryUIList, selectedCategoryId)) return true
        }
        return false
    }
}