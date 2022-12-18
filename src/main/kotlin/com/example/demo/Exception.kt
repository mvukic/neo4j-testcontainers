package com.example.demo

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono


@Order(-2)
@Configuration
class GlobalErrorHandler : WebExceptionHandler {

    override fun handle(serverWebExchange: ServerWebExchange, throwable: Throwable): Mono<Void> {
        val bufferFactory = serverWebExchange.response.bufferFactory()
        serverWebExchange.response.statusCode = HttpStatus.I_AM_A_TEAPOT
        serverWebExchange.response.headers.contentType = MediaType.APPLICATION_JSON
        val body = "{ \"error\": \"${throwable.message ?: "Message"}\"}"
        val dataBuffer = bufferFactory.wrap(body.toByteArray())
        return serverWebExchange.response.writeWith(Mono.just(dataBuffer))
    }

}