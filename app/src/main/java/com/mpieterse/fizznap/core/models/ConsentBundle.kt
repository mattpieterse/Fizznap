package com.mpieterse.fizznap.core.models

import com.mpieterse.fizznap.core.utils.ConsentBundleTranslator

/**
 * Logical version-agnostic aliases for permissions and permission groups.
 *
 * Designed for large, complicated systems, these bundles are tightly coupled to
 * the [ConsentBundleTranslator] where these aliases are converted from abstract
 * ideas into logical permission groups.
 */
enum class ConsentBundle {
    CameraAccess, ImageLibraryAccess
}