package com.example.demo

import com.example.demo.base.DatabaseIntegrationTestsBase
import com.example.demo.data.AddressData
import com.example.demo.data.PersonData
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class SessionIntegrationTests : DatabaseIntegrationTestsBase() {

    @Test
    fun `Count people`() {
        GraphDatabase.driver(container.boltUrl, AuthTokens.none()).use { driver ->
            driver.session().use { session ->
                val people = session.run("MATCH (n: Person) RETURN count(n)").next()[0].asInt()
                assertEquals(PersonData.people.size, people)
            }
        }
    }

    @Test
    fun `Count addresses`() {
        GraphDatabase.driver(container.boltUrl, AuthTokens.none()).use { driver ->
            driver.session().use { session ->
                val addresses = session.run("MATCH (n: Address) RETURN count(n)").next()[0].asInt()
                assertEquals(AddressData.addresses.size, addresses)
            }
        }
    }

    @Test
    fun `Count LIVES_AT relationships`() {
        GraphDatabase.driver(container.boltUrl, AuthTokens.none()).use { driver ->
            driver.session().use { session ->
                val livesAt = session.run("MATCH (n)-[r:LIVES_AT]->() RETURN count(r)").next()[0].asInt()
                assertEquals(AddressData.addresses.size, livesAt)
            }
        }
    }

    @Test
    fun `Count after deletion`() {
        GraphDatabase.driver(container.boltUrl, AuthTokens.none()).use { driver ->
            driver.session().use { session ->
                val personId = PersonData.person1.personId
                session.run("MATCH (n: Person { personId: \"$personId\" }) DETACH DELETE n")
                val peopleCount = session.run("MATCH (n: Person) RETURN count(n)").next()[0].asInt()
                assertEquals(PersonData.people.size - 1, peopleCount)
            }
        }
    }
}