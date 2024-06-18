package com.apollographql.cache.normalized.sql

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.apollographql.cache.normalized.api.NormalizedCache
import com.apollographql.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.cache.normalized.sql.internal.createDriver
import com.apollographql.cache.normalized.sql.internal.createRecordDatabase
import com.apollographql.cache.normalized.sql.internal.getSchema
import java.util.Properties

actual class SqlNormalizedCacheFactory actual constructor(
    private val driver: SqlDriver,
    private val withDates: Boolean,
) : NormalizedCacheFactory() {
  /**
   * @param url Database connection URL in the form of `jdbc:sqlite:path` where `path` is either blank
   * (creating an in-memory database) or a path to a file.
   * @param properties
   */
  @JvmOverloads
  constructor(
      url: String,
      properties: Properties = Properties(),
      withDates: Boolean = false,
  ) : this(JdbcSqliteDriver(url, properties), withDates)

  /**
   * @param name the name of the database or null for an in-memory database
   * @param withDates whether to account for dates in the database.
   * @param baseDir the baseDirectory where to store the database.
   * If [baseDir] does not exist, it will be created
   * If [baseDir] is a relative path, it will be interpreted relative to the current working directory
   */
  constructor(name: String?, withDates: Boolean, baseDir: String?) : this(createDriver(name, baseDir, getSchema(withDates)), withDates)
  actual constructor(name: String?, withDates: Boolean) : this(name, withDates, null)

  actual override fun create(): NormalizedCache {
    return SqlNormalizedCache(createRecordDatabase(driver, withDates))
  }
}

