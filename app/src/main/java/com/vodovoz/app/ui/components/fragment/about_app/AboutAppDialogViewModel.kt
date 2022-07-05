package com.vodovoz.app.ui.components.fragment.about_app

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository

class AboutAppDialogViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    fun fetchUserId() = dataRepository.fetchUserId()

}