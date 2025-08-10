import com.sayeong.vv.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                //_ ksp 플러그인 적용.
                apply("com.google.devtools.ksp")
            }

            pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                dependencies {
                    "implementation"(libs.findLibrary("hilt.core").get())
                }
            }

            pluginManager.withPlugin("com.android.base") {
                pluginManager.apply("com.google.dagger.hilt.android")
                pluginManager.apply( "dagger.hilt.android.plugin")

                dependencies {
                    "implementation"(libs.findLibrary("hilt.android").get())
                    "ksp"(libs.findLibrary("hilt.compiler").get())
                }
            }

        }
    }
}