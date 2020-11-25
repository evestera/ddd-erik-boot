package no.dossier.myapp.controllers

import no.dossier.myapp.domain.Health
import no.dossier.myapp.domain.Node
import no.dossier.myapp.services.NodeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class NodesController(
    private val nodeService: NodeService
) {

  @GetMapping("/nodes")
  fun getNodeUrls(): Set<String> =
      nodeService.getNodes().map { it.url }.toSet()

  @PostMapping("/nodes", "/notify")
  fun postNotify(@RequestBody body: NotifyBody) {
    nodeService.addNode(Node(url = body.url))
  }

  class NotifyBody(val url: String)
  class RestError(
      val tag: String,
      val message: String,
      val status: Int
  ) {
    fun toResponseEntity(): ResponseEntity<RestError> =
        ResponseEntity(this, HttpStatus.valueOf(status))

    companion object {
      fun badRequest(message: String) = RestError(
          tag = "BAD_REQUEST",
          message = message,
          status = 400
      )
    }
  }
}
