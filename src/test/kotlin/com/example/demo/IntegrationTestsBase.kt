package com.example.demo

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.containers.Neo4jLabsPlugin
import org.testcontainers.utility.MountableFile.forClasspathResource

@ContextConfiguration(initializers = [IntegrationTestsBase.Initializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(IntegrationTestsBase.TESTING_PROFILE)
abstract class IntegrationTestsBase {

    @BeforeAll
    fun beforeAll() {
        // Copy .csv files
        listOf("person", "address", "LIVES_AT").forEach {
            container.copyFileToContainer(forClasspathResource("csv/$it.csv"), "$NEO4J_HOME_IMPORT/$it.csv")
        }
        // Copy load script
        container.copyFileToContainer(forClasspathResource("data-loader.cypher"), "$NEO4J_HOME_BIN/data-loader.cypher")
        // Copy bash script
        container.copyFileToContainer(forClasspathResource("run-script.sh"), "$NEO4J_HOME_BIN/run-script.sh")
    }

    @BeforeEach
    fun beforeEach() {
        // Clear old data and import it again
        container.execInContainer("/bin/bash", "$NEO4J_HOME_BIN/run-script.sh").also {
            println(it.stdout)
        }
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            container.start()
            println("http://" + container.host + ":" + container.getMappedPort(7474) + "/browser?dbms=" + container.boltUrl)

            TestPropertyValues.of(
                "spring.neo4j.uri=${container.boltUrl}",
                "spring.neo4j.authentication.username=neo4j",
                "spring.webflux.base-path=/api"
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {

        private const val NEO4J_HOME = "/var/lib/neo4j"
        private const val NEO4J_HOME_IMPORT = "$NEO4J_HOME/import"
        private const val NEO4J_HOME_BIN = "$NEO4J_HOME/bin"

        const val TESTING_PROFILE = "integration-testing"

        @JvmStatic
        protected val container: Neo4jContainer<*> = Neo4jContainer("neo4j:5.2-enterprise")
            .withLabsPlugins(Neo4jLabsPlugin.APOC)
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withEnv("NEO4J_dbms_security_procedures_unrestricted", "apoc.\\*")
            .withEnv("apoc.import.file.enabled", "true")
            .withoutAuthentication()
    }
}