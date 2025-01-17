package com.apollographql.cache.apollocompilerplugin.internal

import com.apollographql.cache.apollocompilerplugin.VERSION
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import kotlin.time.Duration

private object Symbols {
  val MaxAge = ClassName("com.apollographql.cache.normalized.api", "MaxAge")
  val MaxAgeInherit = MaxAge.nestedClass("Inherit")
  val MaxAgeDuration = MaxAge.nestedClass("Duration")
  val Seconds = MemberName(Duration.Companion::class.asTypeName(), "seconds", isExtension = true)
}

internal class Codegen(
    private val packageName: String,
    private val outputDirectory: File,
    private val maxAges: Map<String, Int>,
) {
  fun generate() {
    generateCache()
  }

  private fun generateCache() {
    val initializer = CodeBlock.builder().apply {
      add("mapOf(\n")
      indent()
      maxAges.forEach { (field, duration) ->
        if (duration == -1) {
          addStatement("%S to %T,", field, Symbols.MaxAgeInherit)
        } else {
          addStatement("%S to %T(%L.%M),", field, Symbols.MaxAgeDuration, duration, Symbols.Seconds)
        }
      }
      unindent()
      add(")")
    }
        .build()
    val file = FileSpec.builder(packageName, "Cache")
        .addType(
            TypeSpec.objectBuilder("Cache")
                .addProperty(
                    PropertySpec.builder("maxAges", MAP
                        .parameterizedBy(STRING, Symbols.MaxAge)
                    )
                        .initializer(initializer)
                        .build()
                )
                .build()
        )
        .addFileComment(
            """
                
                AUTO-GENERATED FILE. DO NOT MODIFY.
                
                This class was automatically generated by Apollo GraphQL Cache version '$VERSION'.
                
            """.trimIndent()
        )
        .build()

    file.writeTo(outputDirectory)
  }
}
