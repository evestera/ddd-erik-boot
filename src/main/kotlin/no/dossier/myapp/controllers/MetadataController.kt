package no.dossier.myapp.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MetadataController {

  @GetMapping("/metadata")
  fun getMetadata() = mapOf(
      "name" to "Eriks Spring Boot node",
      "services" to listOf(
          "health",
          "joke",
          "metadata",
          "nodes"
      )
  )
}
