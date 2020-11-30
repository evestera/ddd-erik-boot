package no.dossier.myapp.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MetadataController {

  @GetMapping("/metadata")
  fun getMetadata() = mapOf(
      "name" to "Eriks Spring Boot node",
      "owner" to "Erik Vesteraas",
      "services" to listOf(
          "/health",
          "/joke",
          "/metadata",
          "/nodes",
          "/secure/ping",
          "/secure/pong",
          "/trigger/secure/ping",
          "/.well-known/jwks.json",
          "/widgets",
      )
  )

}
