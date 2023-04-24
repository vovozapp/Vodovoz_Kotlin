package com.vodovoz.app.common.media

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    private val mediaManager: MediaManager
) : ViewModel() {

    fun savePublicationImage(imageFile: File) {
        mediaManager.savePublicationImage(imageFile)
    }

    fun saveAvatarImage(imageFile: File) {
        mediaManager.saveAvatarImage(imageFile)
    }

}
