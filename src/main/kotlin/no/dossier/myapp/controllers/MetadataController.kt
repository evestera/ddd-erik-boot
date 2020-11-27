package no.dossier.myapp.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MetadataController {

  @GetMapping("/metadata")
  fun getMetadata() = mapOf(
      "name" to "Eriks Spring Boot node",
      "services" to listOf(
          "health",
          "joke",
          "metadata",
          "secure/ping",
          "widgets",
          "nodes"
      ).sorted()
  )
}
