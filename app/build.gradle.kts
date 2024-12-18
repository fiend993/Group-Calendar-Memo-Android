plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.coms5540.calendarmemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.coms5540.calendarmemo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    // The view calendar library for Android
    implementation(libs.view)
    // The compose calendar library for Android
    implementation(libs.compose)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.okhttp3)
    implementation(libs.gson)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}