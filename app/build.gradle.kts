plugins {
    alias(libs.plugins.sayeong.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.sayeong.android.hilt)
    alias(libs.plugins.sayeong.android.compose)
}

android {
    namespace = "com.sayeong.vv.sayeongapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sayeong.vv.sayeongapp"
        minSdk = 28
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    //_ feature 모듈 의존성 추가
    implementation(projects.feature.home)
    implementation(projects.feature.player)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
}