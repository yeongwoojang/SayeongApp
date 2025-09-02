import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.sayeong.vv.sayeongapp.buildlogic"
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
    compileOnly(libs.room.gradlePlugin)
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
        register("androidCompose") {
            id = libs.plugins.sayeong.android.compose.get().pluginId
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.sayeong.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.sayeong.android.room.get().pluginId
            implementationClass = "AndroidRoomConventionPlugin"
        }
    }
}

