plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.dapurmoms"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.dapurmoms"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore/dapurmoms.jks")
            storePassword = "dapurmoms123"
            keyAlias = "dapurmoms"
            keyPassword = "dapurmoms123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.core:core-splashscreen:1.0.1")
    // AndroidX Core
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.fragment)

    // Room Database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // UI Components
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // WorkManager for background cleanup
    implementation("androidx.work:work-runtime:2.10.0")
}