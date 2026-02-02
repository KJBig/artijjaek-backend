package com.artijjaek.admin.config.security

import com.artijjaek.admin.config.security.filter.CustomAuthenticationEntryPoint
import com.artijjaek.admin.config.security.filter.ExceptionHandlerFilter
import com.artijjaek.admin.config.security.filter.JwtAuthenticationFilter
import com.artijjaek.core.common.jwt.JwtProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class AdminSecurityConfig(
    private val corsConfigurationSource: CorsConfigurationSource,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val jwtProvider: JwtProvider,
) {

    @Bean
    fun adminFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/admin/**")
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource) }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/admin/v1/auth/login").permitAll()
                    .requestMatchers("/admin/v1/auth/refresh").permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint(customAuthenticationEntryPoint)
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(ExceptionHandlerFilter(), JwtAuthenticationFilter::class.java)

        return http.build()
    }

}