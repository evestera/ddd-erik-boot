package no.dossier.myapp.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WidgetsController {

  @GetMapping("/widgets")
  fun getWidgetsList(): List<String> = listOf()

}
