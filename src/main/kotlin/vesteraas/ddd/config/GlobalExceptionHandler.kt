package vesteraas.ddd.config

import com.auth0.jwt.exceptions.JWTVerificationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.lang.reflect.UndeclaredThrowableException
import java.util.concurrent.ExecutionException
import javax.servlet.http.HttpServletRequest

class ErrorDto(
  val code: ErrorCode,
  val category: ErrorCategory = code.category,
  val details: List<ErrorDetail>? = null,
  val message: String = code.name
)

enum class ErrorCategory {
  CLIENT,      // Your fault
  INTERNAL,    // My fault
  INTEGRATION, // Problem upstream
}

enum class ErrorCode(val status: HttpStatus, val category: ErrorCategory) {
  UNHANDLED(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCategory.INTERNAL),
  INVALID_JWT_TOKEN(HttpStatus.FORBIDDEN, ErrorCategory.CLIENT),
}

class ErrorDetail(
  val key: String,
  val code: ErrorDetailCode,
  val message: String = code.name
)

enum class ErrorDetailCode

@ControllerAdvice
class GlobalExceptionHandler {
  private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

  @ExceptionHandler(Exception::class)
  fun handleException(req: HttpServletRequest, e: Exception): ResponseEntity<ErrorDto> {
    val dto = handleExceptionInner(req, e)
    return ResponseEntity(dto, dto.code.status)
  }

  private fun handleExceptionInner(req: HttpServletRequest, e: Exception): ErrorDto {
    return when (e) {
      is ExecutionException -> handleExceptionInner(req, e.cause as Exception)
      is UndeclaredThrowableException -> handleExceptionInner(req, e.undeclaredThrowable as Exception)

      is JWTVerificationException -> {
        ErrorDto(ErrorCode.INVALID_JWT_TOKEN)
      }

      else -> {
        logger.error("HTTP 500 returned to client due to unhandled exception:", e)
        ErrorDto(ErrorCode.UNHANDLED)
      }
    }
  }
}
