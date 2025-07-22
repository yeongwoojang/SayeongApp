import com.android.build.gradle.LibraryExtension
import kr.co.fastcampus.convention.configureKotlinAndroid
import kr.co.fastcampus.convention.configureKotlinCoroutines
import kr.co.fastcampus.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureKotlinCoroutines()
            }

            dependencies {
                "implementation"(libs.findLibrary("timber").get())
            }
        }
    }
}