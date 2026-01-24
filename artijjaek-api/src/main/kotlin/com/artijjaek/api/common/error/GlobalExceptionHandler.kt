package com.artijjaek.api.common.error

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import com.artijjaek.core.common.error.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val LOG_FORMAT = "Error: {}, Class : {}, Message : {}, Stack : {}"
    private val LOG_CODE_FORMAT = "Error: {}, Code : {}, Message : {}, HttpStatus : {}, Stack : {}"
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * == Application Exception ==
     *
     * @return Each errorCode
     * @throws ApplicationException
     */
    @ExceptionHandler(ApplicationException::class)
    fun applicationException(exception: ApplicationException): ResponseEntity<ErrorResponse> {
        log.error(
            LOG_CODE_FORMAT,
            "ApplicationException",
            exception.code,
            exception.message,
            exception.httpStatus,
            exception.stackTrace
        )

        val httpStatus: HttpStatus = HttpStatus.valueOf(exception.httpStatus)

        return ResponseEntity
            .status(httpStatus)
            .body(ErrorResponse(code = exception.code, message = exception.message))
    }

    /**
     * == 런타임 Exception ==
     *
     * @return INTERNAL_SERVER_ERROR
     * @throws RuntimeException
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun runtimeException(exception: NoResourceFoundException): ResponseEntity<ErrorResponse> {
        log.error(
            LOG_FORMAT,
            "NoResourceFoundException",
            exception.javaClass.getSimpleName(),
            exception.message,
            exception.getStackTrace()
        )

        val errorCode = ErrorCode.API_NOT_FOUND_ERROR

        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse(code = errorCode.code, errorCode.message))
    }

    /**
     * == 런타임 Exception ==
     *
     * @return INTERNAL_SERVER_ERROR
     * @throws RuntimeException
     */
    @ExceptionHandler(RuntimeException::class)
    fun runtimeException(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        log.error(
            LOG_FORMAT,
            "RuntimeException",
            exception.javaClass.getSimpleName(),
            exception.message,
            exception.getStackTrace()
        )

        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR

        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse(code = errorCode.code, errorCode.message))
    }

    /**
     * == 기타 Exception ==
     *
     * @return INTERNAL_SERVER_ERROR
     * @throws Exception
     */
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> {
        log.error(
            LOG_FORMAT,
            "Exception",
            exception.javaClass.getSimpleName(),
            exception.message,
            exception.getStackTrace()
        )

        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR

        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse(code = errorCode.code, errorCode.message))
    }

}
