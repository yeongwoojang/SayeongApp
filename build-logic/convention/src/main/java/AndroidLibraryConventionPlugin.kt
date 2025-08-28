import com.android.build.gradle.LibraryExtension
import com.sayeong.vv.convention.configureKotlinAndroid
import com.sayeong.vv.convention.configureKotlinCoroutines
import com.sayeong.vv.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureKotlinCoroutines()
            }

            dependencies {
                "implementation"(libs.findLibrary("timber").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
            }
        }
    }
}