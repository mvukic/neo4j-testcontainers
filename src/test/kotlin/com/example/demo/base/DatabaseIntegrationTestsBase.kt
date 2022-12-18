package com.example.demo.base

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.containers.Neo4jLabsPlugin
import org.testcontainers.utility.MountableFile.forClasspathResource
import kotlin.test.BeforeTest


@ContextConfiguration(initializers = [DatabaseIntegrationTestsBase.Initializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DatabaseIntegrationTestsBase : IntegrationTestsBase {

    @Autowired
    lateinit var resourcePatternResolver: ResourcePatternResolver

    @BeforeAll
    open fun beforeAll() {
        copyCsvFiles()
        copyScripts()
        loadData()
    }

    @BeforeTest
    open fun beforeEach() {
        loadData()
    }

    private fun copyCsvFiles() {
        resourcePatternResolver
            .getResources("classpath:csv/*.csv")
            .map { it.filename }
            .forEach {
                container.copyFileToContainer(forClasspathResource("csv/${it}"), "$NEO4J_HOME_IMPORT/$it")
            }
    }

    private fun copyScripts() {
        // Copy load script
        container.copyFileToContainer(forClasspathResource("data-loader.cypher"), "$NEO4J_HOME_BIN/data-loader.cypher")
        // Copy bash script
        container.copyFileToContainer(forClasspathResource("run-script.sh"), "$NEO4J_HOME_BIN/run-script.sh")
    }

    private fun loadData() {
        // Clear the existing data in the database and reload new one so that each @Test starts with the fresh data
        container.execInContainer("/bin/bash", "$NEO4J_HOME_BIN/run-script.sh").also {
            println(it.stdout)
        }
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            container.start()
            println("http://" + container.host + ":" + container.getMappedPort(7474) + "/browser?dbms=" + container.boltUrl)

            TestPropertyValues.of(
                "spring.neo4j.uri=${container.boltUrl}"
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {

        private const val NEO4J_HOME = "/var/lib/neo4j"
        private const val NEO4J_HOME_IMPORT = "$NEO4J_HOME/import"
        private const val NEO4J_HOME_BIN = "$NEO4J_HOME/bin"

        @JvmStatic
        protected val container: Neo4jContainer<*> = Neo4jContainer("neo4j:5.2-enterprise")
            .withLabsPlugins(Neo4jLabsPlugin.APOC)
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withEnv("NEO4J_dbms_security_procedures_unrestricted", "apoc.\\*")
            .withEnv("apoc.import.file.enabled", "true")
            .withoutAuthentication()
    }
}