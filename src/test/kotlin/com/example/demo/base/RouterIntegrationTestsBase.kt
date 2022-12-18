package com.example.demo.base

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RouterIntegrationTestsBase : DatabaseIntegrationTestsBase() {

    @LocalServerPort
    private val localServerPort = 0

    protected fun getWebClient() = WebClient.builder().baseUrl("http://localhost:$localServerPort").build()

}

