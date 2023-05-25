package com.vodovoz.app.feature.profile.waterapp.model

import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.profile.waterapp.model.inner.*

object WaterAppLists {

    val firstList = listOf(
        WaterAppModelOne(1),
        WaterAppModelTwo(2),
        WaterAppModelThree(3),
        WaterAppModelFour(4),
        WaterAppModelSeven(5),
        WaterAppModelFive(6),
        WaterAppModelSix(7),
        WaterAppModelSeven(8)
    )

    val notificationShownList = listOf(
        WaterAppModelOne(1),
        WaterAppModelTwo(2),
        WaterAppModelThree(3),
        WaterAppModelFour(4),
        WaterAppModelFive(5),
        WaterAppModelSix(6),
        WaterAppModelSeven(7)
    )

    val innerList = listOf(
        WaterAppModelInnerOne(),
        WaterAppModelInnerTwo(),
        WaterAppModelInnerThree(),
        WaterAppModelInnerFour(),
        WaterAppModelInnerFive(),
        WaterAppModelInnerSix(),
    )

    val innerListIcons = listOf(
        IconWithPosition(1, true),
        IconWithPosition(2, false),
        IconWithPosition(3, false),
        IconWithPosition(4, false),
        IconWithPosition(5, false),
        IconWithPosition(6, false)
    )

    data class IconWithPosition(
        val id: Int,
        val isSelected: Boolean
    )


}