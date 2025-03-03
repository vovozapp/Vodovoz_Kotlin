package com.vodovoz.app.core.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.vodovoz.app.R
import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi

fun NavController.navigateToSubCategories(category: CatalogCategoryUi) {
    navigate(
        R.id.subCategoriesFragment,
        bundleOf("category" to category),
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    )

}