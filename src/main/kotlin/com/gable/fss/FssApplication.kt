package com.gable.fss

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import java.util.*

@SpringBootApplication(
    scanBasePackageClasses = [FssApplication::class],
    exclude = [OAuth2ClientAutoConfiguration::class]
)
open class FssApplication {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FssApplication::class.java)

        val LOCALE: Locale = Locale.US
        val TIMEZONE: TimeZone = TimeZone.getTimeZone("Asia/Bangkok")
    }

    @Autowired
    lateinit var environment: Environment

    @EventListener(ApplicationReadyEvent::class)
    fun ensureProfileOtherThanDefaultIsActive() {
        val defaultProfiles = environment.defaultProfiles
        for (profile in environment.activeProfiles) {
            if (Arrays.stream(defaultProfiles).noneMatch { s: String -> s == profile }) {
                logger.info("Ensured profile other than default is active")
                return
            }
        }
        throw RuntimeException("Expected profiles other than default to be active")
    }

}

private fun <T> ensureAndOrSetDefault(description: String, expected: T, getter: ()->T, setter: (T)->Unit, formatter: T.() -> String) {
    getter().also {
        if (it != getter()) {
            FssApplication.logger.warn(
                """System default $description is $it which is not match expected value ${formatter(expected)}.
                    |Will set it to ${formatter(expected)}.""".trimMargin()
            )
            setter(expected)
            if (getter() != expected) {
                throw Exception("Checked failed!! System default $description not set to ${formatter(expected)} as expected")
            }
            FssApplication.logger.info("System default $description set to ${formatter(expected)} successfully")
        } else {
            FssApplication.logger.info("Ensured system default $description is ${formatter(expected)}")
        }
    }
}

fun main(args: Array<String>) {

    ensureAndOrSetDefault("locale", FssApplication.LOCALE, { Locale.getDefault() }, { value -> Locale.setDefault(value) }, Locale::toString)

    ensureAndOrSetDefault("timezone", FssApplication.TIMEZONE, { TimeZone.getDefault() }, { value -> TimeZone.setDefault(value) }, TimeZone::getID)

    SpringApplication.run(FssApplication::class.java, *args)
}
