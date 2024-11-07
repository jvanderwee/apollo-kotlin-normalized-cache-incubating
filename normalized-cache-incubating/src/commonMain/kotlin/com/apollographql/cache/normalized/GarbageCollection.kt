package com.apollographql.cache.normalized

import com.apollographql.cache.normalized.api.CacheHeaders
import com.apollographql.cache.normalized.api.CacheKey
import com.apollographql.cache.normalized.api.NormalizedCache
import com.apollographql.cache.normalized.api.Record

fun ApolloStore.getReachableCacheKeys(roots: List<CacheKey>, reachableKeys: MutableSet<CacheKey>) {
  val records = accessCache { cache -> cache.loadRecords(roots.map { it.key }, CacheHeaders.NONE) }
  val cacheKeysToCheck = mutableListOf<CacheKey>()
  for (cacheKey in roots) {
    val record = records.firstOrNull { it.key == cacheKey.key } ?: continue
    reachableKeys.add(cacheKey)
    cacheKeysToCheck.addAll(record.referencedFields())
  }
  if (cacheKeysToCheck.isNotEmpty()) {
    getReachableCacheKeys(cacheKeysToCheck, reachableKeys)
  }
}

private fun NormalizedCache.allRecords(): Map<String, Record> {
  return dump()[this::class]!!
}

fun ApolloStore.removeUnreachableRecords() {
  val reachableKeys = mutableSetOf<CacheKey>()
  getReachableCacheKeys(listOf(CacheKey.rootKey()), reachableKeys)
  val allRecords: Map<String, Record> = accessCache { cache -> cache.allRecords() }
  val unreachableCacheKeys = allRecords.keys - reachableKeys.map { it.key }.toSet()
  remove(unreachableCacheKeys.map { CacheKey(it) }, cascade = true)
}
