package test

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.testing.QueueTestNetworkTransport
import com.apollographql.apollo.testing.enqueueTestResponse
import com.apollographql.apollo.testing.internal.runTest
import com.apollographql.cache.normalized.ApolloStore
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.api.CacheKey
import com.apollographql.cache.normalized.fetchPolicy
import com.apollographql.cache.normalized.getReachableCacheKeys
import com.apollographql.cache.normalized.memory.MemoryCacheFactory
import com.apollographql.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.cache.normalized.store
import com.apollographql.cache.normalized.storeReceiveDate
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ReachableCacheKeysTest {
  @Test
  fun getReachableCacheKeys() = runTest {
    val store = ApolloStore(MemoryCacheFactory().chain(SqlNormalizedCacheFactory())).also { it.clearAll() }
    val apolloClient = ApolloClient.Builder().networkTransport(QueueTestNetworkTransport()).store(store)
        .storeReceiveDate(true)
        .build()
    val query = MainQuery(userIds = listOf("42", "43"))
    apolloClient.enqueueTestResponse(
        query,
        MainQuery.Data(
            me = MainQuery.Me(
                __typename = "User",
                id = "0",
                name = "John",
                email = "me@example.com",
                admin = true,
                repositories = listOf(
                    MainQuery.Repository(
                        __typename = "Repository",
                        id = "1",
                    ),
                    MainQuery.Repository(
                        __typename = "Repository",
                        id = "2",
                    ),
                ),
            ),
            users = listOf(
                MainQuery.User(
                    __typename = "User",
                    id = "42",
                    name = "Jane",
                    email = "jane@example.com",
                    admin = false,
                    repositories = listOf(
                        MainQuery.Repository1(
                            __typename = "Repository",
                            id = "3",
                        ),
                        MainQuery.Repository1(
                            __typename = "Repository",
                            id = "4",
                        ),
                    ),
                ),
                MainQuery.User(
                    __typename = "User",
                    id = "43",
                    name = "John",
                    email = "john@example.com",
                    admin = false,
                    repositories = listOf(
                        MainQuery.Repository1(
                            __typename = "Repository",
                            id = "5",
                        ),
                        MainQuery.Repository1(
                            __typename = "Repository",
                            id = "6",
                        ),
                    ),
                ),
            ),
            repositories = listOf(
                MainQuery.Repository2(
                    __typename = "Repository",
                    id = "7",
                ),
                MainQuery.Repository2(
                    __typename = "Repository",
                    id = "8",
                ),
            ),
        ),
    )
    apolloClient.query(query).fetchPolicy(FetchPolicy.NetworkOnly).execute()
    val reachableCacheKeys = mutableSetOf<CacheKey>()
    store.getReachableCacheKeys(listOf(CacheKey.rootKey()), reachableCacheKeys)
    assertContentEquals(
        listOf(
            CacheKey("QUERY_ROOT"),
            CacheKey("Repository:8"),
            CacheKey("Repository:7"),
            CacheKey("User:43"),
            CacheKey("User:42"),
            CacheKey("User:0"),
            CacheKey("Repository:6"),
            CacheKey("Repository:5"),
            CacheKey("Repository:4"),
            CacheKey("Repository:3"),
            CacheKey("Repository:2"),
            CacheKey("Repository:1"),
        ),
        reachableCacheKeys
    )
  }
}
