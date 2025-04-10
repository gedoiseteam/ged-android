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
include(":authentication:domain")
include(":authentication:data")
include(":common")
include(":common:domain")
include(":common:data")
include(":message")
include(":message:domain")
include(":message:data")
include(":news")
include(":news:domain")
include(":news:data")
