package com.artijjaek.admin.config.security

import com.artijjaek.admin.common.auth.AuthAdminIdArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val authAdminIdArgumentResolver: AuthAdminIdArgumentResolver,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(authAdminIdArgumentResolver)
    }

}