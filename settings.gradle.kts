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
        // Repositório Mapbox
            maven {
                url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
                authentication {
                    create<BasicAuthentication>("basic")
                }
                credentials {
                    username = "mapbox"
                    password = "sk.eyJ1Ijoicm9nZXJpb2xpbWFqcmwiLCJhIjoiY21pOTZjYzl5MDd3NzJscTA4a3dxNmE5ZSJ9.yv-Io0SVnR3tX7M3ktT-UQ"
                }
            }
    }
}

rootProject.name = "Geocomms"
include(":app")
 