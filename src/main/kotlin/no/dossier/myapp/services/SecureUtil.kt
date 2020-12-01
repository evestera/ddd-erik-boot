package no.dossier.myapp.services

import org.springframework.http.HttpHeaders
import javax.servlet.http.HttpServletRequest

fun HttpServletRequest.extractBearerAuth(): String {
  val authHeader = this.getHeader(HttpHeaders.AUTHORIZATION)
      ?: throw IllegalArgumentException("Authorization header was missing")
  if (!authHeader.startsWith("Bearer ")) {
    throw IllegalArgumentException("Authorization header was not Bearer")
  }
  return authHeader.substring("Bearer ".length)
}
