package com.vodovoz.app.feature.home.viewholders.homebanners.inneradapter

import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.feature.home.viewholders.homebanners.model.BannerAdvEntity

interface HomeBannersSliderClickListener {

    fun onBannerClick(entity: ActionEntity?)
    fun onBannerAdvClick(entity: BannerAdvEntity?)
}