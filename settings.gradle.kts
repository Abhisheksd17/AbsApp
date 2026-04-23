pluginManagement {
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

rootProject.name = "AbsApp"
include(":app")
include(":core:ui")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:model")
include(":feature-auth")
include(":feature-chat")
include(":feature-profile")
include(":feature-notifications")
include(":feature-settings")
include(":core:data")
include(":core:domain")
include(":feature-contact")
include(":feature-contacts")
include(":feature-call")
