import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "kr.co.fastcampus.sayeongapp.buildlogic"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

// plugin class 체크하는 task 설정
tasks {
    validatePlugins {
        // 플러그인 클래스가 올바르게 정의되었는지 확인
        enableStricterValidation = true
        // 경고가 발생하면 빌드를 실패로 처리
        failOnWarning = true
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.android.tools.common)
    implementation(libs.truth)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.sayeong.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.sayeong.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = libs.plugins.sayeong.android.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
    }
}

