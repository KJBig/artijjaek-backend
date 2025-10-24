package com.artijjaek.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.setAllowedOrigins(
            mutableListOf<String?>(
                "http://localhost:5173",
            )
        )
        configuration.addAllowedHeader("Content-Type")
        configuration.addAllowedMethod("*")
        configuration.setAllowCredentials(true)
        configuration.setMaxAge(3600L)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}