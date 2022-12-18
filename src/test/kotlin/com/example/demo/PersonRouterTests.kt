package com.example.demo

import com.example.demo.base.RouterIntegrationTestsBase
import com.example.demo.data.PersonData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.reactive.function.client.awaitExchange
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PersonRouterTests : RouterIntegrationTestsBase() {


    @BeforeTest
    override fun beforeEach() {
        super.beforeEach()
    }

    @Test()
    @WithUserDetails(
        value = "integration-tests-user",
        userDetailsServiceBeanName = "integrationTestingUserDetailService"
    )
    fun `get all people`() = runTest {
        val client = getWebClient()
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
    @WithMockUser(
        username = "integration-tests",
        password = "password",
        authorities = ["USER"]
    )
    fun `get all people with addresses`() = runTest {
        val client = getWebClient()
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
    @WithMockUser(
        username = "integration-tests",
        password = "password",
        authorities = ["USER"]
    )
    fun `get all people where name starts with a prefix`(prefix: String, count: Int) = runTest {
        val client = getWebClient()
        client.get()
            .uri {
                it.path(GET_ALL_WHERE_NAME_STARTS_WITH_PATH)
                    .queryParam("prefix", prefix)
                    .build()
            }
            .awaitExchange { response ->
                val body = response.awaitBodyOrNull<List<PersonModel>>()
                assertNotNull(body)
                assertEquals(count, body.size)
            }
    }

    companion object {
        private const val BASE_PATH = "/api/person"
        private const val GET_ALL_WHERE_NAME_STARTS_WITH_PATH = "$BASE_PATH/starts-with"
        private const val GET_ALL_WITH_ADDRESSES_PATH = "$BASE_PATH/with-addresses"
    }

}