package com.example.demo

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PersonRouter(
    private val personRepository: PersonRepository
) {

    @Bean
    fun personRouterFn() = coRouter {
        GET("/person") {
            val people = personRepository.getAll().asFlow().map { it.toModel() }
            ServerResponse.ok().bodyAndAwait(people)
        }
        GET("/person/starts-with") { request ->
            val prefix = request.queryParam("prefix").orElseThrow()
            val people = personRepository.getAllNameStartsWith(prefix).asFlow().map { it.toModel() }
            ServerResponse.ok().bodyAndAwait(people)
        }
        GET("/person/with-addresses") {
            val people = personRepository.getAllWithAddresses().asFlow().map { it.toModel() }
            ServerResponse.ok().bodyAndAwait(people)
        }
    }

}