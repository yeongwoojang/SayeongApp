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
                "implementation"(libs.findLibrary("kotlinx.coroutines.core").get())
            }
        }
    }
}