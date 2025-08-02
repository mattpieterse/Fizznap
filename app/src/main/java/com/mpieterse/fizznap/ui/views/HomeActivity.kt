package com.mpieterse.fizznap.ui.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mpieterse.fizznap.GuardActivity
import com.mpieterse.fizznap.R
import com.mpieterse.fizznap.core.coordinators.ConsentCoordinator
import com.mpieterse.fizznap.core.models.ConsentBundle
import com.mpieterse.fizznap.core.models.ConsentUiHost
import com.mpieterse.fizznap.core.models.ImageResult
import com.mpieterse.fizznap.core.services.DeviceCaptureService
import com.mpieterse.fizznap.core.services.DeviceGalleryService
import com.mpieterse.fizznap.core.utils.Clogger
import com.mpieterse.fizznap.core.utils.ImageFileUtil
import com.mpieterse.fizznap.databinding.ActivityHomeBinding
import com.mpieterse.fizznap.ui.adapters.GalleryAdapter
import com.mpieterse.fizznap.ui.models.Clickable
import com.mpieterse.fizznap.ui.viewmodels.HomeViewModel
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), Clickable, ConsentUiHost {
    companion object {
        private const val TAG = "HomeActivity"
    }


    private lateinit var binds: ActivityHomeBinding
    private val model: HomeViewModel by viewModels()


    private lateinit var adapter: GalleryAdapter
    private lateinit var captureService: DeviceCaptureService
    private lateinit var galleryService: DeviceGalleryService
    private val sourceDirectory: File by lazy {
        ImageFileUtil.getStorageDirectory(this)
    }


// --- Lifecycle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Clogger.d(
            TAG, "Created a new instance of $TAG"
        )

        setupBindings()
        setupLayoutUi()
        setupTouchListeners()
        observe()
        
        requestPermissions()

        adapter = GalleryAdapter().apply {
            submitList(loadImagesFromDisk())
        }

        captureService = DeviceCaptureService(this)
        captureService.registerForLauncherResult() { result ->
            when (result) {
                is ImageResult.Success -> {
                    adapter.insert(result.fileUri.toString())
                    binds.rvImages.scrollToPosition(0)
                }

                is ImageResult.Failure -> {
                    Toast.makeText(
                        this, result.message.toString(), Toast.LENGTH_LONG
                    ).show()
                }

                is ImageResult.Blocked -> {
                    Toast.makeText(
                        this, result.message.toString(), Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        galleryService = DeviceGalleryService(this)
        galleryService.registerForLauncherResult { result ->
            when (result) {
                is ImageResult.Success -> {
                    adapter.insert(result.fileUri.toString())
                    binds.rvImages.scrollToPosition(0)
                }

                is ImageResult.Failure -> {
                    Toast.makeText(
                        this, result.message.toString(), Toast.LENGTH_LONG
                    ).show()
                }

                is ImageResult.Blocked -> {
                    Toast.makeText(
                        this, result.message.toString(), Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binds.rvImages.layoutManager = LinearLayoutManager(this)
        binds.rvImages.adapter = adapter
    }


//    override fun onStop() {
//        super.onStop()
//        if (!isChangingConfigurations) {
//            secureApplication()
//        }
//    }


// --- ViewModel


    private fun observe() {}


// --- Internals


    private fun requestPermissions() = ConsentCoordinator.requestConsent(
        caller = this, uiHost = this, consentBundles = arrayOf(
            ConsentBundle.CameraAccess, ConsentBundle.ImageLibraryAccess
        )
    )

    
    private fun loadImagesFromDisk(): List<String> {
        val images = sourceDirectory.listFiles { file ->
            file.extension == "jpg"
        }

        return images?.sortedByDescending {
            it.lastModified()
        }?.map {
            it.toUri().toString()
        } ?: emptyList()
    }
    

    private fun launchCaptureIntent() {
        Clogger.d(
            TAG, "Launching camera intent"
        )

        captureService.launchCamera()
    }


    private fun launchGalleryIntent() {
        Clogger.d(
            TAG, "Launching gallery intent"
        )

        galleryService.launchPicker()
    }


    private fun secureApplication() {
        startActivity(Intent(this, GuardActivity::class.java))
        finishAffinity()
    }


// --- Event Handlers (UI)


    override fun setupTouchListeners() {
        binds.fabCapture.setOnClickListener(this)
        binds.fabGallery.setOnClickListener(this)
    }


    override fun onClick(view: View?) = when (view?.id) {
        binds.fabCapture.id -> launchCaptureIntent()
        binds.fabGallery.id -> launchGalleryIntent()
        else -> {
            Clogger.w(
                TAG, "Unhandled on-click for: ${view?.id}"
            )
        }
    }


// --- Event Handlers (Permission)


    override fun onShowInitialConsentUi(
        scope: ExplainScope, declinedTemporarily: List<String>
    ) {
        scope.showRequestReasonDialog(
            permissions = declinedTemporarily,
            getString(R.string.permx_explain_scope_description),
            getString(R.string.permx_explain_scope_on_positive),
            getString(R.string.permx_explain_scope_on_negative)
        )
    }


    override fun onShowWarningConsentUi(
        scope: ForwardScope, declinedPermanently: List<String>
    ) {
        scope.showForwardToSettingsDialog(
            permissions = declinedPermanently,
            getString(R.string.permx_forward_scope_description),
            getString(R.string.permx_forward_scope_on_positive),
            getString(R.string.permx_forward_scope_on_negative)
        )
    }


    override fun onConsentsAccepted(
        accepted: List<String>
    ) {
        Clogger.i(
            TAG, "Consents accepted: $accepted"
        )

        binds.fabCapture.isEnabled = true
        binds.fabGallery.isEnabled = true
    }


    override fun onConsentsDeclined(
        declined: List<String>
    ) {
        Clogger.i(
            TAG, "Consents declined: $declined"
        )

        requestPermissions()
    }


// --- UI


    private fun setupBindings() {
        binds = ActivityHomeBinding.inflate(layoutInflater)
        Clogger.d(
            TAG, "View binding attached to $TAG"
        )
    }


    private fun setupLayoutUi() {
        setContentView(binds.root)
        enableEdgeToEdge()

        // Apply system-bar insets to the root view
        ViewCompat.setOnApplyWindowInsetsListener(binds.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binds.fabCapture.isEnabled = false
        binds.fabGallery.isEnabled = false
    }
}