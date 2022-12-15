package com.example.demo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@OptIn(ExperimentalCoroutinesApi::class)
class PersonRepositoryTests(
    @Autowired private val repository: PersonRepository
) : IntegrationTestBase() {

    @Test
    fun `Count people`() = runTest {
        val people = repository.count().awaitSingle()
        assertEquals(3, people)
    }

    @Test
    fun `Count people with addresses`() = runTest {
        val people = repository.getWithAddresses().asFlow().toList()
        assertEquals(3, people.size)
    }

}