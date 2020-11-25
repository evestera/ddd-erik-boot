package no.dossier.myapp.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HealthController {

  @GetMapping("/health")
  fun getHealth() = mapOf("status" to "OK")

}
