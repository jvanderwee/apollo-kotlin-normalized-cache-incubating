package test

import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.jsonReader
import com.apollographql.apollo.testing.internal.runTest
import com.apollographql.cache.normalized.api.TypePolicyCacheKeyGenerator
import com.apollographql.cache.normalized.api.normalize
import okio.Buffer
import schema.changes.GetFieldQuery
import kotlin.test.Test

class SchemaChangesTest {
  @Test
  fun schemaChanges() = runTest {
    val operation = GetFieldQuery()

    @Suppress("UNUSED_VARIABLE")
    val v1Data = """
      {
        "field": {
          "__typename": "DefaultField",
          "id": "1",
          "name": "Name1"
        }
      }
    """.trimIndent()

    val v2Data = """
      {
        "field": {
          "__typename": "NewField",
          "id": "1",
          "name": "Name1"
        }
      }
    """.trimIndent()

    val data = operation.adapter().fromJson(
        Buffer().writeUtf8(v2Data).jsonReader(),
        CustomScalarAdapters.Empty
    )

    operation.normalize(
        data,
        customScalarAdapters = CustomScalarAdapters.Empty,
        cacheKeyGenerator = TypePolicyCacheKeyGenerator,
    )
  }
}