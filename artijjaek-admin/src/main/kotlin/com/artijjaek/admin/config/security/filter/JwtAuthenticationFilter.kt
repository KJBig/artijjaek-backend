package com.artijjaek.admin.config.security.filter

import com.artijjaek.core.common.jwt.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)

        if (token != null) {
            SecurityContextHolder.getContext().authentication = getAuthentication(token)
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization") ?: return null
        if (!header.startsWith("Bearer ")) return null
        return header.removePrefix("Bearer ").trim()
    }

    private fun getAuthentication(token: String): Authentication {
        val adminId = jwtProvider.parseAccessToken(token).subject.toLong()
        val adminDetails = AdminDetails(adminId)
        return UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.authorities)
    }
}

