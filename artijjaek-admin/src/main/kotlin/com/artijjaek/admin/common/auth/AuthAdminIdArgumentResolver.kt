package com.artijjaek.admin.common.auth

import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthAdminIdArgumentResolver : HandlerMethodArgumentResolver {

    private val log = LoggerFactory.getLogger(AuthAdminIdArgumentResolver::class.java)

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentAdminId::class.java) &&
                parameter.parameterType == Long::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication != null && authentication.name != "anonymousUser") {
            val adminId = authentication.name.toLong()
            log.info("== Admin Id : {} ==", adminId)
            return adminId
        }

        return null
    }
}
