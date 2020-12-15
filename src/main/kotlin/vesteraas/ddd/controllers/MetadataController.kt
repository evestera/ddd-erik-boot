package vesteraas.ddd.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MetadataController {

  @GetMapping("/metadata")
  fun getMetadata() = mapOf(
      "name" to "Eriks Spring Boot node",
      "owner" to "Erik Vesteraas",
      "description" to "Node written in Kotlin using Spring Boot 2 and deployed on Heroku",
      "services" to listOf(
          "/health",
          "/joke",
          "/metadata",
          "/nodes",
          "/secure/hello",
          "/secure/ping",
          "/secure/pong",
          "/trigger/secure/ping",
          "/.well-known/jwks.json",
          "/widgets",
          "/secure/persist/nodes/sync",
          "/secure/persist/nodes/unregister",
      )
  )

}
