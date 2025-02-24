package com.vodovoz.app.core.android

import androidx.navigation.NavController
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.domain.general.model.DataAllAction
import com.vodovoz.app.domain.general.model.VodovozAction
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.home.HomeFragmentDirections
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment

fun DataAllAction.activate(
    navController: NavController,
    activators: List<DataAllActionActivator> = emptyList(),
) {
    val currentActivator = activators.firstOrNull { it.action == this }

    if (currentActivator != null) {
        currentActivator.activate()
        return
    }

    when (this) {
        DataAllAction.AllDiscount -> {
            navController.navigate(
                HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.HurryBuyUpProducts
                )
            )
        }

        DataAllAction.AllNewProducts -> {
            navController.navigate(
                HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.NewProducts
                )
            )
        }

        DataAllAction.AllPromotions -> {
            navController.navigate(
                HomeFragmentDirections.actionToAllPromotionsFragment(
                    AllPromotionsFragment.DataSource.All
                )
            )
        }

        DataAllAction.Delivery -> {
            navController.navigate(
                HomeFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_DELIVERY_URL,
                    "О доставке"
                )
            )
        }

        DataAllAction.Profile -> {

        }

        DataAllAction.WaterTracker -> {
            navController.navigate(
                HomeFragmentDirections.actionToWaterAppFragment()
            )
        }

        DataAllAction.Unknown -> {

        }
    }
}

fun ButtonAction.activate(
    navController: NavController,
    activateIdAction: ((Int) -> Unit)? = null,
    activators: List<DataAllActionActivator> = emptyList(),
) {
    when (this) {
        is ButtonAction.Action -> {
            value.activate(navController, activators)
        }

        is ButtonAction.Id -> activateIdAction?.let {
            activateIdAction(id)
        }
    }
}

fun VodovozAction.activate(
    navController: NavController,
    activators: List<VodovozActionActivator> = emptyList(),
) {
    when (this) {
        is VodovozAction.Brand -> TODO()
        is VodovozAction.Category -> TODO()
        is VodovozAction.Product -> TODO()
        is VodovozAction.Products -> TODO()
        is VodovozAction.Promotion -> TODO()
        is VodovozAction.Promotions -> TODO()
        is VodovozAction.Url -> TODO()
        is VodovozAction.UrlWithCookie -> TODO()
        is DataAllAction -> {
            val dataAllActivators = activators.mapNotNull { it as? DataAllActionActivator }
            activate(navController, dataAllActivators)
        }

        is VodovozAction.Unknown -> TODO()
    }
}

open class Activator<out T>(
    val action: T,
    private val activate: (T) -> Unit,
) {

    fun activate() = activate(action)

    override fun equals(other: Any?): Boolean {
        return action == other
    }

    override fun hashCode(): Int {
        return action?.hashCode() ?: 0
    }

}

inline fun <reified T> createActivator(
    action: T,
    noinline activate: (T) -> Unit,
): Activator<T> {
    return Activator(action, activate)
}

open class VodovozActionActivator(
    action: VodovozAction,
    activate: (VodovozAction) -> Unit,
) : Activator<VodovozAction>(action, activate)

inline fun <reified T : VodovozAction> createVodovozActivator(
    action: T,
    noinline activate: (T) -> Unit,
): VodovozActionActivator {
    return VodovozActionActivator(action = action, activate = { activate(action) })
}

class DataAllActionActivator(
    action: DataAllAction,
    activate: (DataAllAction) -> Unit,
) : VodovozActionActivator(action, activate = { activate(action) })

inline fun <reified T : DataAllAction> createDataAllActivator(
    action: T,
    noinline activate: (T) -> Unit,
): DataAllActionActivator {
    return DataAllActionActivator(action = action, activate = { activate(action) })
}



