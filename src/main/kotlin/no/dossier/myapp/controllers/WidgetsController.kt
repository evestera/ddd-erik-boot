package no.dossier.myapp.controllers

import no.dossier.myapp.config.ServerConfiguration
import no.dossier.myapp.domain.Widget
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WidgetsController(
    private val server: ServerConfiguration
) {

  @GetMapping("/widgets")
  fun getWidgetsList(): List<Widget> = listOf(
      Widget(
          name = "Hello World",
          description = "A widget that just renders the message 'Hello World'",
          selector = ".widget-erik-boot-hello-world",
          script = "${server.url}/widget-hello-world.js"
      ),
      Widget(
          name = "Nodes List",
          description = "A widget that fetches and lists /nodes on the application it is rendered in",
          selector = ".widget-erik-boot-own-nodes",
          script = "${server.url}/widget-own-nodes.js"
      ),
      Widget(
          name = "Describe Self",
          description = "A widget that fetches and shows /metadata for the application it is rendered in",
          selector = ".widget-erik-boot-describe-self",
          script = "${server.url}/widget-describe-self.js"
      )
  )

}
