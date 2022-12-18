package com.example.demo.base

import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(IntegrationTestsBase.TESTING_PROFILE)
interface IntegrationTestsBase {

    companion object {
        const val TESTING_PROFILE = "integration-testing"
    }

}