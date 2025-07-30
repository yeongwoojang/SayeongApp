package com.sayeong.vv.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("composeCompiler").get().toString()
        }
        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()

            "implementation"(libs.findLibrary("androidx.activity.compose").get())
            "implementation"(platform(bom))
            "androidTestImplementation"(platform(bom))
            "implementation"(libs.findLibrary("androidx.ui").get())
            "implementation"(libs.findLibrary("androidx.ui.graphics").get())
            "implementation"(libs.findLibrary("androidx.ui.tooling.preview").get())
            "implementation"(libs.findLibrary("androidx.material3").get())
            "debugImplementation"(libs.findLibrary("androidx.ui.tooling").get())
        }


    }
}