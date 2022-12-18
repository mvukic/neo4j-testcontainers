package com.example.demo

import com.example.demo.base.IntegrationTestsBase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class AppPropertiesTests(
    @Autowired private val appProperties: AppProperties
) : IntegrationTestsBase {

    @Test
    fun `app properties in test environment`() {
        assertEquals("integration-testing-value", appProperties.value)
        assertEquals("nested-integration-testing-value", appProperties.nested.value)
    }

}