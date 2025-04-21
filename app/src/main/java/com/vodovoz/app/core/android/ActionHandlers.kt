package com.vodovoz.app.core.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.CookieManager
import androidx.navigation.NavController
import com.vodovoz.app.R
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToBannerProductList
import com.vodovoz.app.core.navigation.navigateToBrandProductList
import com.vodovoz.app.core.navigation.navigateToButtonProductList
import com.vodovoz.app.core.navigation.navigateToBuyCertificate
import com.vodovoz.app.core.navigation.navigateToCategoryProductList
import com.vodovoz.app.core.navigation.navigateToHurryBuyUpProducts
import com.vodovoz.app.core.navigation.navigateToNewProducts
import com.vodovoz.app.core.navigation.navigateToProductDetails
import com.vodovoz.app.core.navigation.navigateToPromotionDetails
import com.vodovoz.app.core.navigation.navigateToPromotions
import com.vodovoz.app.core.navigation.navigateToWaterApp
import com.vodovoz.app.core.navigation.navigateToWebView
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.core.network.VODOVOZ_URL
import com.vodovoz.app.core.network.VodovozWebConfig
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.domain.general.model.DataAllAction
import com.vodovoz.app.domain.general.model.VodovozAction

fun DataAllAction.activate(
    navController: NavController,
    tabManager: TabManager,
    activators: List<DataAllActionActivator> = emptyList(),
) {
    val currentActivator = activators.firstOrNull { it.action == this }

    if (currentActivator != null) {
        currentActivator.activate()
        return
    }

    when (this) {
        DataAllAction.AllDiscount -> {
            navController.navigateToHurryBuyUpProducts()
        }

        DataAllAction.AllNewProducts -> {
            navController.navigateToNewProducts()
        }

        DataAllAction.AllPromotions -> {
            navController.navigateToPromotions()
        }

        DataAllAction.Delivery -> {
            navController.navigateToWebView(VodovozWebConfig.ABOUT_PAYMENT_URL, " ")
        }

        DataAllAction.Profile -> {
            tabManager.setAuthRedirect(navController.graph.id)
            tabManager.selectTab(R.id.graph_profile)
        }

        DataAllAction.WaterTracker -> {
            navController.navigateToWaterApp()
        }

        DataAllAction.BuyCertificate -> {
            navController.navigateToBuyCertificate()
        }

        DataAllAction.Unknown -> {

        }

    }
}

fun ButtonAction.activate(
    navController: NavController,
    tabManager: TabManager,
    activators: List<DataAllActionActivator> = emptyList(),
) {
    when (this) {
        is ButtonAction.Action -> {
            value.activate(navController, tabManager, activators)
        }

        is ButtonAction.Id -> {
            navController.navigateToButtonProductList(id)
        }
    }
}

fun VodovozAction.activate(
    navController: NavController,
    activity: Activity,
    cookie: String,
    tabManager: TabManager,
    activators: List<VodovozActionActivator> = emptyList(),
) {
    val currentActivator = activators.firstOrNull { it.action == this }

    if (currentActivator != null) {
        currentActivator.activate()
        return
    }

    when (this) {
        is VodovozAction.Brand -> {
            navController.navigateToBrandProductList(id)
        }

        is VodovozAction.Category -> {
            navController.navigateToCategoryProductList(id)
        }

        is VodovozAction.Product -> {
            navController.navigateToProductDetails(id)
        }

        is VodovozAction.Products -> {
            navController.navigateToBannerProductList(bannerId, blockId)
        }

        is VodovozAction.Promotion -> {
            navController.navigateToPromotionDetails(id)
        }

        is VodovozAction.Promotions -> {
            navController.navigateToPromotions(blockId, bannerId)
        }

        is VodovozAction.Url -> {
            runCatching {
                val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                activity.startActivity(openLinkIntent)
            }
        }

        is VodovozAction.UrlWithCookie -> {
            val webCookieManager = CookieManager.getInstance()
            webCookieManager.acceptCookie()
            webCookieManager.setCookie(VODOVOZ_URL, cookie)
            navController.navigateToWebView(url, "")
        }

        is DataAllAction -> {
            val dataAllActivators = activators.mapNotNull { it as? DataAllActionActivator }
            activate(navController, tabManager, dataAllActivators)
        }

        is VodovozAction.Unknown -> {
            /**
             * You can create activator or do something here
             * */
        }
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



