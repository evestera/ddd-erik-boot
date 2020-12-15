package vesteraas.ddd.controllers

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import vesteraas.ddd.config.ServerConfiguration
import vesteraas.ddd.services.TokenService
import vesteraas.ddd.services.extractBearerAuth
import javax.servlet.http.HttpServletRequest

@RestController
class SecureHelloController(
  private val tokenService: TokenService,
  private val server: ServerConfiguration
) {

  private val logger = LoggerFactory.getLogger(SecureHelloController::class.java)

  @PostMapping("/secure/hello")
  fun postSecurePing(request: HttpServletRequest): String {
    val validatedToken = tokenService.validateToken(request.extractBearerAuth(), server.url)
    logger.info("Secure hello request received from ${validatedToken.issuer}")
    return "Hello ${validatedToken.issuer}"
  }

}
