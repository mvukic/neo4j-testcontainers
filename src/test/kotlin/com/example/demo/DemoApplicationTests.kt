package com.example.demo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DemoApplicationTests : IntegrationTestBase() {

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
                assertEquals(2L, addresses)
            }
        }
    }

    @Test
    fun `Count LIVES_AT relationships`() {
        val boltUrl: String = container.boltUrl
        GraphDatabase.driver(boltUrl, AuthTokens.none()).use { driver ->
            driver.session().use { session ->
                val livesAt = session.run("MATCH (n: Address)-[r:LIVES_AT]-() RETURN count(r)").next()[0].asLong()
                assertEquals(3L, livesAt)
            }
        }
    }


}