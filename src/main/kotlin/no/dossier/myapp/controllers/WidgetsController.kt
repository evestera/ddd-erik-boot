package no.dossier.myapp.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WidgetsController {

  @GetMapping("/widgets")
  fun getWidgetsList(): List<String> = listOf()

}
