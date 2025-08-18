import com.sayeong.vv.convention.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.sayeong.vv.convention.libs
import org.gradle.kotlin.dsl.dependencies

class JvmLibraryConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("java-library")
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            configureKotlinJvm()

            dependencies {
                //_ 순수 kotlin 모듈에서 공통적으로 coroutine을 사용할 수 있도록 처리
                "implementation"(libs.findLibrary("kotlinx.coroutines.core").get())
            }
        }
    }
}