package com.vodovoz.app.common.media

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.util.extensions.intArgs
import com.vodovoz.app.util.extensions.millisToItemDate
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ImagePickerFragment : BaseFragment() {

    companion object {
        private const val READ_EXTERNAL = Manifest.permission.READ_EXTERNAL_STORAGE
        const val CREATE = 1
        const val AVATAR = 2
        const val IMAGE_PICKER_RECEIVER = "image picker receiver"
    }

    private val receiver by intArgs(IMAGE_PICKER_RECEIVER)

    override fun layout(): Int = R.layout.fragment_image_picker

    private val viewModel: ImagePickerViewModel by viewModels()

    private val storagePermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onPermissionResult(isGranted)
        }

    private val getPictureFromGalleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                if (uri != null) {
                    val file = saveFileFromGallery(uri)
                    when(receiver) {
                        CREATE -> viewModel.savePublicationImage(file)
                        AVATAR -> viewModel.saveAvatarImage(file)
                    }
                }
            }
            navigateUp()
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storagePermission.launch(READ_EXTERNAL)
    }

    private fun saveFileFromGallery(uri: Uri) : File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = createImageFile()
        file.outputStream().use {
            inputStream?.copyTo(it)
        }
        return file
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis().millisToItemDate()
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpeg", storageDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return file
    }

    private fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            ImagePicker.with(this)
                .galleryOnly()
                .apply {
                    when(receiver) {
                        CREATE -> crop(1f, 1f)
                        AVATAR -> cropSquare()
                    }
                }
                .createIntent { intent ->
                    getPictureFromGalleryResultLauncher.launch(intent)
                }
        } else {
            initPermissionRationale()
        }
    }

    private fun initPermissionRationale() {
        val needPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            READ_EXTERNAL
        )
        if (needPermissionRationale) {
            showPermissionRationaleDialog()
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Чтобы добавить фото, нужен доступ к хранилищу")
            .setPositiveButton("ОК") { _, _ -> storagePermission.launch(READ_EXTERNAL) }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
