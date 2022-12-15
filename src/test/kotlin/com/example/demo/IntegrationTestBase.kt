package com.example.demo

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.containers.Neo4jLabsPlugin
import org.testcontainers.utility.MountableFile

@ContextConfiguration(initializers = [IntegrationTestBase.Initializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTestBase {

    @BeforeAll
    fun beforeAll() {
        // Copy data to container
        container.copyFileToContainer(MountableFile.forClasspathResource("csv/person.csv"), "$NEO4J_HOME_IMPORT/person.csv")
        container.copyFileToContainer(MountableFile.forClasspathResource("csv/address.csv"), "$NEO4J_HOME_IMPORT/address.csv")
        container.copyFileToContainer(
            MountableFile.forClasspathResource("data-loader.cypher"),
            "$NEO4J_HOME_BIN/data-loader.cypher"
        )
        container.copyFileToContainer(
            MountableFile.forClasspathResource("run-script.sh"),
            "$NEO4J_HOME_BIN/run-script.sh"
        )
        container.execInContainer("$NEO4J_HOME/bin/run-script.sh").also {
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
                "spring.profiles.active=integration-testing"
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {

        private const val NEO4J_HOME = "/var/lib/neo4j"
        private const val NEO4J_HOME_IMPORT = "$NEO4J_HOME/import"
        private const val NEO4J_HOME_BIN = "$NEO4J_HOME/bin"

        @JvmStatic
        protected val container = Neo4jContainer("neo4j:5.2-enterprise")
            .withLabsPlugins(Neo4jLabsPlugin.APOC)
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withEnv("NEO4J_dbms_security_procedures_unrestricted", "apoc.\\*")
            .withEnv("apoc.import.file.enabled", "true")
            .withoutAuthentication()
    }
}