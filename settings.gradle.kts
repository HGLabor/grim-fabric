pluginManagement {
	repositories {
		mavenCentral()
		maven(url = "https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		gradlePluginPortal()
	}
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "grim-fabric"
include(":grim-api")
include(":grim-mod")
