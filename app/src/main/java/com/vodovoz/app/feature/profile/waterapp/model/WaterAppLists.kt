package com.vodovoz.app.feature.profile.waterapp.model

import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.profile.waterapp.model.inner.*
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerHeight
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerSleepTime
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerWakeTime
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerWeight

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

    val listOfHeight = (9..201).map {
        PickerHeight(it)
    }

    val listOfWeight = (9..201).map {
        PickerWeight(it)
    }

    val listOfSleepTime = (-10..1440).step(10).map {
        PickerSleepTime(it)
    }

    val listOfWakeTimeTime = (-10..1440).step(10).map {
        PickerWakeTime(it)
    }

    data class IconWithPosition(
        val id: Int,
        val isSelected: Boolean
    )


}