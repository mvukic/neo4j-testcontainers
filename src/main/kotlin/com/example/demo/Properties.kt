package com.example.demo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
data class AppProperties(
    var value: String = "",
    var nested: AppPropertiesNested = AppPropertiesNested()
)

data class AppPropertiesNested(
    var value: String = ""
)