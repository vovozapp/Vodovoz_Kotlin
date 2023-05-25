package com.vodovoz.app.feature.profile.waterapp.adapter

interface WaterAppClickListener {

    fun onNextClick(currentPosition: Int)
    fun onNextClick(currentPosition: Int, saveData: Boolean)
    fun onPrevClick(currentPosition: Int)

    fun saveGender(gender: String)
    fun saveHeight(height: String)
    fun saveWeight(weight: String)
    fun saveSleepTime(sleepTime: String)
    fun saveWakeUpTime(wakeUpTime: String)
    fun saveSport(sport: String)
}