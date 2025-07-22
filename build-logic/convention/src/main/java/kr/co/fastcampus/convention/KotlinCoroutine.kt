package kr.co.fastcampus.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Kotlin 코루틴 설정을 위한 확장 함수
 * 코루틴 의존성 추가 및 관련 컴파일러 옵션 설정
 */
internal fun Project.configureKotlinCoroutines() {
    // 코루틴 관련 컴파일러 옵션 설정
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            // 실험적 코루틴 API 사용 허용
            freeCompilerArgs.addAll(listOf(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview"
            ))
        }
    }

    // 코루틴 의존성 추가
    dependencies {
        // 코어 코루틴 의존성
        "implementation"(libs.findLibrary("kotlinx.coroutines.core").get())
        "implementation"(libs.findLibrary("kotlinx.coroutines.android").get())

        // 테스트용 코루틴 의존성
        "testImplementation"(libs.findLibrary("kotlinx.coroutines.test").get())
    }
}