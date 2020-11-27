package no.dossier.myapp.controllers

import no.dossier.myapp.domain.Widget
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WidgetsController {

  @GetMapping("/widgets")
  fun getWidgetsList(): List<Widget> = listOf(
      Widget(
          name = "Hello World",
          description = "A widget that just renders the message 'Hello World'",
          selector = ".widget-erik-boot-hello-world",
          script = "https://ddd-erik-boot.herokuapp.com/widget-hello-world.js"
      )
  )

}

