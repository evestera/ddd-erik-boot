package no.dossier.myapp.controllers

import no.dossier.myapp.config.ServerConfiguration
import no.dossier.myapp.services.Jwks
import no.dossier.myapp.services.TokenService
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import javax.servlet.http.HttpServletRequest

@RestController
class SecurePingController(
    private val tokenService: TokenService,
    private val restTemplate: RestTemplate,
    private val server: ServerConfiguration
) {

  private val logger = LoggerFactory.getLogger(SecurePingController::class.java)

  @PostMapping("/trigger/secure/ping")
  fun postTriggerPing(@RequestBody requestBody: TriggerPingBody): PingPongResponse {
    logger.info("Sending PING to ${requestBody.url}")
    val response = makeSecureRequest<Any>(requestBody.url, "/secure/ping", "PING")
    return PingPongResponse("PING sent and got response with status ${response.statusCode}")
  }

  @GetMapping("/.well-known/jwks.json")
  fun getJwks(): Jwks {
    logger.info("JWKS requested")
    return tokenService.getJwks()
  }

  @PostMapping("/secure/ping")
  fun postSecurePing(request: HttpServletRequest): PingPongResponse {
    val validatedToken = tokenService.validateToken(extractBearerAuth(request), server.url)
    logger.info("PING received from: ${validatedToken.issuer}. Sending PONG back.")
    val response = makeSecureRequest<Any>(validatedToken.issuer, "/secure/pong", "PONG")
    return PingPongResponse("PING received, PONG sent and got response with status ${response.statusCode}")
  }

  @PostMapping("/secure/pong")
  fun postSecurePong(request: HttpServletRequest): PingPongResponse {
    val validatedToken = tokenService.validateToken(extractBearerAuth(request), server.url)
    logger.info("PONG received from ${validatedToken.issuer}")
    return PingPongResponse("PONG received")
  }

  class PingPongResponse(val result: String)

  class TriggerPingBody(val url: String)

  private inline fun <reified T> makeSecureRequest(url: String, path: String, requestBody: Any): ResponseEntity<T> {
    return restTemplate.exchange(
        url + path,
        HttpMethod.POST,
        HttpEntity(requestBody, HttpHeaders().apply {
          setBearerAuth(tokenService.getToken(server.url, url))
        })
    )
  }

  private fun extractBearerAuth(request: HttpServletRequest): String {
    val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        ?: throw IllegalArgumentException("Authorization header was missing")
    if (!authHeader.startsWith("Bearer ")) {
      throw IllegalArgumentException("Authorization header was not Bearer")
    }
    return authHeader.substring("Bearer ".length)
  }
}
