package com.example.demo

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PersonRepository : ReactiveNeo4jRepository<PersonEntity, String> {

    @Query("MATCH (p: Person) RETURN p")
    fun getAll(): Flux<PersonEntity>

    @Query(
        "MATCH (p: Person)-[livesAt:LIVES_AT]->(a:Address) " +
                "RETURN p, collect(livesAt), collect(a)"
    )
    fun getAllWithAddresses(): Flux<PersonEntity>

    @Query("MATCH (p: Person) WHERE p.name STARTS WITH \$prefix RETURN p")
    fun getAllNameStartsWith(@Param("prefix") prefix: String): Flux<PersonEntity>

}