package vesteraas.ddd.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import vesteraas.ddd.domain.Health

@RestController
class HealthController {

  @GetMapping("/health")
  fun getHealth() = Health("OK")

}
