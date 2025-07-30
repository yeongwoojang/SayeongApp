
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import kr.co.fastcampus.convention.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidComposeConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            //_ "com.android.library" 플러그인이 적용된 경우에만 동작
            pluginManager.withPlugin("com.android.library") {
                val extension = extensions.getByType<LibraryExtension>()
                configureCompose(extension)
            }

            //_ "com.android.application" 플러그인이 적용된 경우에만 동작
            pluginManager.withPlugin("com.android.application") {
                val extension = extensions.getByType<ApplicationExtension>()
                configureCompose(extension)
            }
        }
    }
}