package no.dossier.myapp.controllers

import no.dossier.myapp.services.Jwks
import no.dossier.myapp.services.TokenService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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
