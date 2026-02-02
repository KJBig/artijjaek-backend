package com.artijjaek.admin.config.security.filter

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class ExceptionHandlerFilter(
    private val objectMapper: ObjectMapper = ObjectMapper()
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(ExceptionHandlerFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: ApplicationException) {
            setErrorResponse(
                response = response,
                message = e.message,
                code = e.code,
                status = HttpStatus.valueOf(e.httpStatus)
            )
        }
    }

    @Throws(IOException::class)
    private fun setErrorResponse(
        response: HttpServletResponse,
        message: String,
        code: String,
        status: HttpStatus
    ) {
        log.error("Code : {}, Message : {}", code, message)

        val errorResponse = objectMapper.writeValueAsString(
            ErrorResponse(code = code, message = message)
        )

        response.status = status.value()
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
        response.writer.write(errorResponse)
    }
}