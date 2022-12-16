package com.example.demo

import org.neo4j.driver.Driver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager
import org.springframework.data.neo4j.repository.config.ReactiveNeo4jRepositoryConfigurationExtension
import org.springframework.transaction.ReactiveTransactionManager
import java.util.*


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
