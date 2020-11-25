package no.dossier.myapp.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

  @GetMapping("/health")
  fun getHealth() = mapOf("status" to "OK")

}
