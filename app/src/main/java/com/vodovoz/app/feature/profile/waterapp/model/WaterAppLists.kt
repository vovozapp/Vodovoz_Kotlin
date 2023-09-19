package com.vodovoz.app.feature.profile.waterapp.model

import com.vodovoz.app.feature.profile.waterapp.model.inner.*
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.*

object WaterAppLists {

    val firstList = listOf(
        WaterAppModelOne(2),
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

    val startedList = listOf(
        WaterAppModelFive(1),
        WaterAppModelSix(2),
        WaterAppModelSeven(3)
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

    val durationLists = listOf(
        PickerDuration(15),
        PickerDuration(30),
        PickerDuration(45),
        PickerDuration(60),
        PickerDuration(90),
        PickerDuration(120),
        PickerDuration(150),
        PickerDuration(180),
        PickerDuration(210),
        PickerDuration(240)
    )

    data class IconWithPosition(
        val id: Int,
        val isSelected: Boolean,
    )


}