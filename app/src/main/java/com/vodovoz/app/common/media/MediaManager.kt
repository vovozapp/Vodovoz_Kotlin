package com.vodovoz.app.common.media

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaManager @Inject constructor() {

    private val publicationImageListener = MutableStateFlow<List<File>?>(null)
    private val avatarImageListener = MutableStateFlow<File?>(null)

    fun observePublicationImage() = publicationImageListener.asStateFlow()
    fun observeAvatarImage(): StateFlow<File?> = avatarImageListener.asStateFlow()

    fun saveAvatarImage(imageFile: File) {
        avatarImageListener.value = imageFile
    }

    fun removeAvatarImage() {
        avatarImageListener.value = null
    }

    fun savePublicationImage(files: List<File>) {
        publicationImageListener.value = files
    }

    fun removePublicationImage() {
        publicationImageListener.value = null
    }

}
