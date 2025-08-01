package com.mpieterse.fizznap.core.services

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.mpieterse.fizznap.core.models.ImageResult
import com.mpieterse.fizznap.core.models.ImageResult.Blocked
import com.mpieterse.fizznap.core.models.ImageResult.Failure
import com.mpieterse.fizznap.core.models.ImageResult.Success
import com.mpieterse.fizznap.core.utils.Clogger
import com.mpieterse.fizznap.core.utils.ImageFileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Service to select an image from the device gallery.
 *
 * @param caller The reference to the Fragment or Activity that is calling the
 *        abstract service. This is necessary for functions using Android File
 *        APIs that require a valid context to be called from.
 */
class DeviceGalleryService(
    caller: FragmentActivity
) : DeviceImageService(caller) {

    private lateinit var galleryLauncher: ActivityResultLauncher<String>


    /**
     * Invokes the device gallery image picker.
     */
    fun launchPicker() {
        Clogger.i(
            TAG, "Started launching the picker"
        )

        galleryLauncher.launch(MULTIMEDIA_TYPE)
    }


    override fun registerForLauncherResult(
        callback: (ImageResult) -> Unit
    ) {
        Clogger.i(
            TAG, "Started registering for launcher result"
        )

        this.callback = callback
        galleryLauncher = caller.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri == null) {
                val e = Exception(BLOCKED_MESSAGE)
                Clogger.d(TAG, BLOCKED_MESSAGE)
                callback(Blocked(e))
            } else {
                caller.lifecycleScope.launch {
                    val directory = createImageFile()
                    val completed = ImageFileUtil.copyUriToDirectory(
                        caller, uri, directory
                    )

                    withContext(Dispatchers.Main) {
                        if (completed) {
                            callback(Success(directory.toUri()))
                        } else {
                            val e = Exception(FAILURE_MESSAGE)
                            Clogger.e(TAG, FAILURE_MESSAGE, e)
                            callback(Failure(e))
                        }
                    }
                }
            }
        }
    }


    private companion object {
        const val TAG = "DeviceGalleryService"
        const val BLOCKED_MESSAGE = "No image was selected"
        const val FAILURE_MESSAGE = "Failed to copy image to directory"
        const val MULTIMEDIA_TYPE = "image/*"
    }
}