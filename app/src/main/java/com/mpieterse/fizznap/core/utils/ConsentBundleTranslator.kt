package com.mpieterse.fizznap.core.utils

import com.mpieterse.fizznap.core.models.ConsentBundle

/**
 * Translator to bidirectionally convert consents and permissions.
 */
object ConsentBundleTranslator {

    /**
     * Convert the consent bundle to a collection of permission strings.
     */
    fun toAndroid(bundle: ConsentBundle): Array<String> {
        return when (bundle) {
            ConsentBundle.CameraAccess -> ConsentVersionHelper.getCameraPermissionCode()
            ConsentBundle.ImageLibraryAccess -> ConsentVersionHelper.getImagePermissionCodes()
        }
    }
}