package com.mpieterse.fizznap.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocalApplication : Application() {
    companion object {
        private const val TAG = "LocalApplication"
    }
}