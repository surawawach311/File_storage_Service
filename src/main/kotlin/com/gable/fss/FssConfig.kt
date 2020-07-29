package com.gable.fss

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "fss")
open class FssConfig {
    lateinit var rootLocation: String
}
