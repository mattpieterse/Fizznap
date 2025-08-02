package com.mpieterse.fizznap

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mpieterse.fizznap.core.commands.BiometricTransactionCommand
import com.mpieterse.fizznap.core.models.BiometricUiHost
import com.mpieterse.fizznap.core.utils.Clogger
import com.mpieterse.fizznap.databinding.ActivityGuardBinding
import com.mpieterse.fizznap.ui.views.HomeActivity

class GuardActivity : AppCompatActivity(), BiometricUiHost {
    companion object {
        private const val TAG = "GuardActivity"
    }
    

    private lateinit var binds: ActivityGuardBinding


// --- Lifecycle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Clogger.d(
            TAG, "Created a new instance of $TAG"
        )

        setupBindings()
        setupLayoutUi()
        unlock()
    }


    override fun onRestart() {
        super.onRestart()
        unlock()
    }


// --- Internals


    private fun unlock() {
        BiometricTransactionCommand(
            caller = this, uiHost = this
        ).execute()
    }


// --- Event Handlers (Biometrics)


    override fun onShowBiometrics(
        uiBuilder: BiometricPrompt.PromptInfo.Builder
    ) {
        uiBuilder.apply {
            setTitle(getString(R.string.biometrics_scope_title))
            setDescription(getString(R.string.biometrics_scope_description))
            setNegativeButtonText(getString(R.string.biometrics_scope_on_negative))
        }
    }


    override fun onBiometricsSucceeded() {
        startActivity(Intent(this, HomeActivity::class.java))
        finishAffinity()
    }


    override fun onBiometricsDismissed() {
        unlock()
    }


    override fun onBiometricsException(
        code: Int, message: String
    ) {
        Clogger.e(
            TAG, message
        )

        finishAffinity()
    }


// --- UI


    private fun setupBindings() {
        binds = ActivityGuardBinding.inflate(layoutInflater)
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
    }
}