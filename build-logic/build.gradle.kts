import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverExtension
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverGradleSubplugin

plugins {
  `embedded-kotlin`
  id("java-gradle-plugin")
}

plugins.apply(SamWithReceiverGradleSubplugin::class.java)
extensions.configure(SamWithReceiverExtension::class.java) {
  annotations(HasImplicitReceiver::class.qualifiedName!!)
}

group = "com.apollographql.cache.build"

dependencies {
  compileOnly(gradleApi())
  compileOnly(libs.kotlin.plugin)
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<JavaCompile>().configureEach {
  options.release.set(17)
}

tasks.withType(KotlinJvmCompile::class.java).configureEach {
  kotlinOptions.jvmTarget = "17"
}

gradlePlugin {
  plugins {
    register("build.logic") {
      id = "build.logic"
      // This plugin is only used for loading the jar using the Marker but never applied
      // We don't need it.
      implementationClass = "build.logic.Unused"
    }
  }
}