package com.gable.fss

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*


@Configuration
open class LocaleConfiguration : InitializingBean {
    /**
     * This bean is for servlets
     */
    @Bean
    open fun localeResolver(): LocaleResolver {
        val localeResolver = SessionLocaleResolver()
        localeResolver.setDefaultLocale(FssApplication.LOCALE)
        return localeResolver
    }

    override fun afterPropertiesSet() {
        Locale.setDefault(FssApplication.LOCALE)
    }
}
