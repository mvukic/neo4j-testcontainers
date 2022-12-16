package com.example.demo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@OptIn(ExperimentalCoroutinesApi::class)
class PersonRepositoryTests(
    @Autowired private val repository: PersonRepository
) : IntegrationTestsBase() {

    @Test
    fun `Count people`() = runTest {
        val people = repository.count().awaitSingle()
        assertEquals(3, people)
    }

    @Test
    fun `Count people with addresses`() = runTest {
        val people = repository.getAllWithAddresses().asFlow().toList()
        assertEquals(3, people.size)
        val personWithTwoAddresses = people.find { it.personId == "75e29665-69c6-4053-a195-2a26dd44ea32" }
        assertNotNull(personWithTwoAddresses)
        assertEquals(2, personWithTwoAddresses.addresses.size)
    }

}