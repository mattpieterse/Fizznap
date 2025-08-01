plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

// - Configuration

android {
    namespace = "com.mpieterse.fizznap"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mpieterse.fizznap"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "initial"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

// - Libraries

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Firebase

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.perf)

    // Hilt

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // PermissionX

    implementation(libs.permissionx)

    // Google Play Services

    implementation(libs.play.services.auth)
    implementation(libs.play.services.base)

    // Biometric Authentication
    
    implementation(libs.androidx.biometric)
}