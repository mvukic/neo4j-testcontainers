package com.example.demo

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PersonRouter(
    private val handler: PersonRouterHandler
) {

    @Bean
    fun personRouterFn() = coRouter {
        GET("/person", handler::getAll)
        GET("/person/with-addresses", handler::getAllWithAddresses)
        GET("/person/starts-with", handler::getAllNameStartWith)
    }

}

@Service
class PersonRouterHandler(
    private val personRepository: PersonRepository
) {

    suspend fun getAll(request: ServerRequest): ServerResponse {
//        println("User is ${getCurrentUser()}")
        val result = personRepository.getAll().asFlow().map { it.toModel() }
        return ServerResponse.ok().bodyAndAwait(result)
    }

    suspend fun getAllWithAddresses(request: ServerRequest): ServerResponse {
//        println("User is ${getCurrentUser()}")
        val result = personRepository.getAllWithAddresses().asFlow().map { it.toModel() }
        return ServerResponse.ok().bodyAndAwait(result)
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    suspend fun getAllNameStartWith(request: ServerRequest): ServerResponse {
//        println("User is ${getCurrentUser()}")
        val prefix = request.queryParam("prefix").orElseThrow()
        val result = personRepository.getAllNameStartsWith(prefix).asFlow().map { it.toModel() }
        return ServerResponse.ok().bodyAndAwait(result)
    }

}