package no.dossier.myapp.controllers

import no.dossier.myapp.domain.Health
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

  @GetMapping("/health")
  fun getHealth() = Health("OK")

}
