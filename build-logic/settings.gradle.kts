rootProject.name = "build-logic"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}

apply(from = "../gradle/repositories.gradle.kts")