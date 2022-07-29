package com.vodovoz.app.ui.fragment.about_app

import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository

class AboutAppDialogViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    fun fetchUserId() = dataRepository.fetchUserId()

}