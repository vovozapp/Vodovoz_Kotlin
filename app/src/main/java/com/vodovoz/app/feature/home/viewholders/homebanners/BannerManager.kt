package com.vodovoz.app.feature.home.viewholders.homebanners

import javax.inject.Inject
import javax.inject.Singleton

interface BannerManager {
    fun fetchPosition() : Int
    fun setPosition(pos: Int)
    fun increase()
}

@Singleton
class TopBannerManager @Inject constructor() : BannerManager {

    private var position = 0

    override fun fetchPosition(): Int {
        return position
    }

    override fun setPosition(pos: Int) {
        position = pos
    }

    override fun increase() {
        position++
    }
}

@Singleton
class BottomBannerManager @Inject constructor() : BannerManager {

    private var position = 0

    override fun fetchPosition(): Int {
        return position
    }

    override fun setPosition(pos: Int) {
        position = pos
    }

    override fun increase() {
        position++
    }
}