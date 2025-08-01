package com.mpieterse.fizznap.core.services

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.mpieterse.fizznap.core.models.ImageResult
import com.mpieterse.fizznap.core.models.ImageResult.Blocked
import com.mpieterse.fizznap.core.models.ImageResult.Failure
import com.mpieterse.fizznap.core.models.ImageResult.Success
import com.mpieterse.fizznap.core.utils.Clogger

/**
 * Service to capture an image from the device camera.
 *
 * @param caller The reference to the Fragment or Activity that is calling the
 *        abstract service. This is necessary for functions using Android File
 *        APIs that require a valid context to be called from.
 */
class DeviceCaptureService(
    caller: FragmentActivity
) : DeviceImageService(caller) {


    private lateinit var captureLauncher: ActivityResultLauncher<Uri>
    private var imageUri: Uri? = null


    /**
     * Invokes the device camera with the file provider.
     */
    fun launchCamera() {
        Clogger.i(
            TAG, "Started launching the camera"
        )

        val file = createImageFile()
        imageUri = FileProvider.getUriForFile(
            caller, "${caller.packageName}.fileprovider", file
        )

        imageUri?.let {
            Clogger.i(
                TAG, "Launching camera with file provider: $imageUri"
            )

            captureLauncher.launch(it)
        } ?: {
            val e = Exception(LAUNCHER_FAILURE_MESSAGE)
            Clogger.e(TAG, LAUNCHER_FAILURE_MESSAGE, e)
            callback?.invoke(Failure(e))
        }
    }


    override fun registerForLauncherResult(
        callback: (ImageResult) -> Unit
    ) {
        Clogger.i(
            TAG, "Started registering for launcher result"
        )

        this.callback = callback
        captureLauncher = caller.registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            val result: ImageResult = if (success.not()) {
                val e = Exception(BLOCKED_MESSAGE)
                Clogger.d(TAG, BLOCKED_MESSAGE)
                Blocked(e)
            } else {
                when (imageUri != null) {
                    true -> {
                        Clogger.i(
                            TAG, "Successfully captured image from camera: $imageUri"
                        )

                        Success(imageUri!!)
                    }

                    else -> {
                        val e = Exception(REGISTER_FAILURE_MESSAGE)
                        Clogger.e(TAG, REGISTER_FAILURE_MESSAGE, e)
                        Failure(e)
                    }
                }
            }

            callback(result)
        }
    }


    private companion object {
        const val TAG = "DeviceCaptureService"
        const val BLOCKED_MESSAGE = "Image capture was cancelled"
        const val REGISTER_FAILURE_MESSAGE = "Something went wrong with the camera"
        const val LAUNCHER_FAILURE_MESSAGE = "Failed to create the image file"
    }
}