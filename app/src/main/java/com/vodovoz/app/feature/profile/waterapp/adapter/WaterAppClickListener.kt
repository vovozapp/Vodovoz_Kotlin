package com.vodovoz.app.feature.profile.waterapp.adapter

interface WaterAppClickListener {

    fun onNextClick(currentPosition: Int)
    fun onNextClick(currentPosition: Int, saveData: Boolean)
    fun onPrevClick(currentPosition: Int)
}