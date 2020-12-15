package vesteraas.ddd.controllers

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import vesteraas.ddd.services.Jwks
import vesteraas.ddd.services.TokenService

@RestController
class JwksController(
    private val tokenService: TokenService
) {

  private val logger = LoggerFactory.getLogger(JwksController::class.java)

  @GetMapping("/.well-known/jwks.json")
  fun getJwks(): Jwks {
    logger.info("JWKS requested")
    return tokenService.getJwks()
  }

}
