package com.vodovoz.app.ui.fragment.about_app

import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutAppDialogViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    fun fetchUserId() = dataRepository.fetchUserId()

}