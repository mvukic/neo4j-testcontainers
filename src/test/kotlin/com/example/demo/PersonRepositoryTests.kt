package com.example.demo

import com.example.demo.base.DatabaseIntegrationTestsBase
import com.example.demo.data.PersonData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@OptIn(ExperimentalCoroutinesApi::class)
class PersonRepositoryTests(
    @Autowired private val repository: PersonRepository
) : DatabaseIntegrationTestsBase() {

    @Test
    fun `Count people`() = runTest {
        val peopleCount = repository.count().awaitSingle()
        assertEquals(PersonData.people.size.toLong(), peopleCount)
    }

    @Test
    fun `Count people with addresses`() = runTest {
        val people = repository.getAllWithAddresses().asFlow().toList()
        assertEquals(PersonData.people.size, people.size)
        val personWithTwoAddresses = people.find { it.personId == PersonData.person3.personId }
        assertNotNull(personWithTwoAddresses)
        assertEquals(PersonData.people.size - 1, personWithTwoAddresses.addresses.size)
    }

}