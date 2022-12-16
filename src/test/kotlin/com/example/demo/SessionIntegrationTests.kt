package com.example.demo

import com.example.demo.data.PersonData
import org.junit.jupiter.api.Test
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class SessionIntegrationTests : IntegrationTestsBase() {

    @Test
    fun `Count people`() {
        val boltUrl: String = container.boltUrl
        GraphDatabase.driver(boltUrl, AuthTokens.none()).use { driver ->
            driver.session().use { session ->
                val people = session.run("MATCH (n: Person) RETURN count(n)").next()[0].asLong()
                assertEquals(3L, people)
            }
        }
    }

    @Test
    fun `Count addresses`() {
        val boltUrl: String = container.boltUrl
        GraphDatabase.driver(boltUrl, AuthTokens.none()).use { driver ->
            driver.session().use { session ->
                val addresses = session.run("MATCH (n: Address) RETURN count(n)").next()[0].asLong()
                assertEquals(4L, addresses)
            }
        }
    }

    @Test
    fun `Count LIVES_AT relationships`() {
        val boltUrl: String = container.boltUrl
        GraphDatabase.driver(boltUrl, AuthTokens.none()).use { driver ->
            driver.session().use { session ->
                val livesAt = session.run("MATCH (n)-[r:LIVES_AT]->() RETURN count(r)").next()[0].asLong()
                assertEquals(4L, livesAt)
            }
        }
    }

    @Test
    fun `Count after deletion`()  {
        val boltUrl: String = container.boltUrl
        GraphDatabase.driver(boltUrl, AuthTokens.none()).use { driver ->
            driver.session().use { session ->
                val personId = PersonData.person1.personId
                session.run("MATCH (n: Person { personId: \"$personId\" }) DETACH DELETE n")
                val people = session.run("MATCH (n: Person) RETURN count(n)").next()[0].asLong()
                assertEquals(2L, people)
            }
        }
    }
}