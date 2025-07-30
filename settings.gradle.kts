pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

//_ 멀티 모듈 프로젝트에서 다른 모듈에 의존성을 추가할 때, 문자열 대신 타입 안전한 방식으로 접근할 수 있도록 함.
//_ projects.core.model과 같은 형식으로 모듈 참조 가능
//_ 문자열 기반 참조(project(":core:model")) 대신 타입 안전한 참조 제공
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SayeongApp"

include(":app")
include(":core:data")
include(":core:domain")
include(":feature:home")
include(":feature:player")
include(":core:network")
