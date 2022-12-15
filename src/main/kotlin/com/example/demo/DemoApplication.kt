package com.example.demo

import kotlinx.coroutines.reactive.asFlow
import org.neo4j.driver.Driver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Version
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.Relationship
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.config.ReactiveNeo4jRepositoryConfigurationExtension
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter
import reactor.core.publisher.Flux
import java.util.*


@Node("Person")
class PersonNode(
    @Property("name")
    var name: String
) {

    @Id
    @Property("personId")
    var personId: String = UUID.randomUUID().toString()

    @Version
    @Property("dbVersion")
    var dbVersion: Long = 0

    @Relationship(type = "LIVES_AT", direction = Relationship.Direction.OUTGOING)
    var addresses: MutableList<AddressNode> = mutableListOf()
}

@Node("Address")
class AddressNode(
    @Property("number")
    var number: Int,

    @Property("street")
    var street: String
) {

    @Id
    @Property("addressId")
    var addressId: String = UUID.randomUUID().toString()

    @Version
    @Property("dbVersion")
    var dbVersion: Long = 0
}

@Repository
interface PersonRepository : ReactiveNeo4jRepository<PersonNode, String> {

    @Query("MATCH (p: Person)-[livesAt:LIVES_AT]->(a:Address) return p, livesAt, a")
    fun getWithAddresses(): Flux<PersonNode>

}

@Configuration
class PersonRouter(
    private val personRepository: PersonRepository
) {

    @Bean
    fun personRouterFn() = coRouter {
        GET("/person") {
            val people = personRepository.findAll().asFlow()
            ServerResponse.ok().bodyAndAwait(people)
        }
    }

}

@SpringBootApplication
class DemoApplication {
    @Bean(ReactiveNeo4jRepositoryConfigurationExtension.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
    fun reactiveTransactionManager(
        driver: Driver,
        databaseNameProvider: ReactiveDatabaseSelectionProvider
    ): ReactiveTransactionManager {
        return ReactiveNeo4jTransactionManager(driver, databaseNameProvider)
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
