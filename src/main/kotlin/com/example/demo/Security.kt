package com.example.demo

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

enum class AppRole {
    USER,
    ADMIN
}

data class AppUser(
    val username: String,
    val roles: List<AppRole>
)

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebSecurityConfig(
    private val securityContextRepository: SecurityContextRepository
) {

    @Bean
    fun webFilterChainFn(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .anyExchange().authenticated()
            .and()
            .securityContextRepository(securityContextRepository)
            .build()
    }

}

@Component
class SecurityContextRepository : ServerSecurityContextRepository {

    override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val header = exchange.request.headers[HttpHeaders.AUTHORIZATION]?.firstOrNull()
            ?: error("Header not found")
        val role = exchange.request.headers["role"]?.firstOrNull()?.uppercase()
            ?: error("role not found")
        val user = AppUser(username = header, roles = listOf(enumValueOf(role)))
        return SecurityContextImpl(
            UsernamePasswordAuthenticationToken(
                header,
                header,
                listOf(SimpleGrantedAuthority(role))
            ).apply {
                details = user
            }
        ).toMono()
    }
}


suspend fun getCurrentUser(): AppUser {
    return ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.details as AppUser
}