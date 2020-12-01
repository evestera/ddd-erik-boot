package no.dossier.myapp.controllers

import no.dossier.myapp.config.ServerConfiguration
import no.dossier.myapp.services.TokenService
import no.dossier.myapp.services.extractBearerAuth
import no.dossier.myapp.services.normalizeUrl
import org.slf4j.LoggerFactory
import org.springframework.http.*
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
    val targetUrl = normalizeUrl(requestBody.url)
    logger.info("Sending PING to $targetUrl")
    val response = makeSecureRequest<Any>(targetUrl, "/secure/ping", "PING")
    return PingPongResponse("PING sent and got response with status ${response.statusCode}")
  }

  @PostMapping("/secure/ping")
  fun postSecurePing(request: HttpServletRequest): PingPongResponse {
    val validatedToken = tokenService.validateToken(request.extractBearerAuth(), server.url)
    val sourceUrl = normalizeUrl(validatedToken.issuer)
    logger.info("PING received from: $sourceUrl. Sending PONG back.")
    val response = makeSecureRequest<Any>(sourceUrl, "/secure/pong", "PONG")
    return PingPongResponse("PING received, PONG sent and got response with status ${response.statusCode}")
  }

  @PostMapping("/secure/pong")
  fun postSecurePong(request: HttpServletRequest): PingPongResponse {
    val validatedToken = tokenService.validateToken(request.extractBearerAuth(), server.url)
    val sourceUrl = normalizeUrl(validatedToken.issuer)
    logger.info("PONG received from $sourceUrl")
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
}
