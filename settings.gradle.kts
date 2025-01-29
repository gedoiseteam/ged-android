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

rootProject.name = "Gedoise"
include(":app")
include(":authentication")
include(":common")
include(":news")
include(":message")
include(":message:data")
include(":common:data")
include(":news:data")
include(":news:domain")
include(":common:domain")
include(":message:domain")
include(":authentication:data")
include(":authentication:domain")
