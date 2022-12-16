package com.example.demo

import com.example.demo.data.PersonData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.reactive.function.client.awaitExchange
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@OptIn(ExperimentalCoroutinesApi::class)
class PersonRouterTests : IntegrationTestsBase() {

    @LocalServerPort
    private val localServerPort = 0

    @Test()
    fun `get all people`() = runTest {
        val client = WebClient.builder().baseUrl("http://localhost:$localServerPort").build()
        client.get()
            .uri(BASE_PATH)
            .awaitExchange {
                val response = it.awaitBodyOrNull<List<PersonModel>>()
                assertNotNull(response)
                assertEquals(response.size, PersonData.people.size)
                response.forEach {
                    assertTrue { it.addresses.isEmpty() }
                }
            }
    }

    @Test()
    fun `get all people with addresses`() = runTest {
        val client = WebClient.builder().baseUrl("http://localhost:$localServerPort").build()
        client.get()
            .uri(GET_ALL_WITH_ADDRESSES_PATH)
            .awaitExchange {
                val response = it.awaitBodyOrNull<List<PersonModel>>()
                assertNotNull(response)
                assertEquals(response.size, PersonData.people.size)
                response.forEach {
                    assertTrue { it.addresses.isNotEmpty() }
                }
            }
    }

    @ParameterizedTest
    @CsvSource(value = ["P, 2", "O, 1", "A, 0"])
    fun `get all people where name starts with a prefix`(prefix: String, count: Int) = runTest {
        val client = WebClient.builder().baseUrl("http://localhost:$localServerPort").build()
        client.get()
            .uri {
                it.path(GET_ALL_WHERE_NAME_STARTS_WITH_PATH)
                    .queryParam("prefix", prefix)
                    .build()
            }
            .awaitExchange { response ->
                val body = response.awaitBodyOrNull<List<PersonModel>>()
                assertNotNull(body)
                assertEquals(body.size, count)
            }
    }

    companion object {
        private const val BASE_PATH = "/api/person"
        private const val GET_ALL_WHERE_NAME_STARTS_WITH_PATH = "$BASE_PATH/starts-with"
        private const val GET_ALL_WITH_ADDRESSES_PATH = "$BASE_PATH/with-addresses"
    }

}