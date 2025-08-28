
import com.android.build.api.dsl.ApplicationExtension
import com.sayeong.vv.convention.configureKotlinAndroid
import com.sayeong.vv.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            //_ 해당 부분은 build.gradle.kt에서 android {...} 블록과 의미가 같다.
            //_ targetSdk는 ApplicationExtension만 접근 할 수 있으므로 configureKotlinAndroid에서 설정하지 않고 아래의 코드에서 설정한다.
            //_ configureKotlinAndroid에서는 CommonExtension을 받아서 처리한다. 따라서 targetSdk에 접근이 불가능 (targetSdk는 ApplicationExtension에서만 접근 가능하다.)
            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
                @Suppress("UnstableApiUsage")
                testOptions.animationsDisabled = true

                dependencies {
                    "implementation"(libs.findLibrary("timber").get())
                    "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
                }
            }
        }
    }
}