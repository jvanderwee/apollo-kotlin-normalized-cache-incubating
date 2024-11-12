plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.apollo)
}

kotlin {
  configureKmp(
      withJs = false,
      withWasm = false,
      withAndroid = false,
      withApple = AppleTargets.Host,
  )

  sourceSets {
    getByName("commonMain") {
      dependencies {
        implementation(libs.apollo.runtime)
        implementation("com.apollographql.cache:normalized-cache-sqlite-incubating")
      }
    }

    getByName("commonTest") {
      dependencies {
        implementation(libs.apollo.testing.support)
        implementation(libs.apollo.mockserver)
        implementation(libs.kotlin.test)
      }
    }
  }
}

apollo {
  service("service") {
    packageName.set("test")
  }
}
