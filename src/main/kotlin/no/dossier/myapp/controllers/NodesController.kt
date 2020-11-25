package no.dossier.myapp.controllers

import no.dossier.myapp.domain.Node
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@RestController
class NodesController(
    private val restTemplate: RestTemplate
) {

  private val nodes = mutableSetOf<Node>()

  @GetMapping("/nodes")
  fun getNodes(): Set<String> = nodes.map { it.url }.toSet()

  @PostMapping("/nodes", "/notify")
  fun postNotify(@RequestBody body: NotifyBody): ResponseEntity<out Any> {
    val node = Node(url = body.url)
    val response = restTemplate.getForEntity<Health>(node.url + "/health")
    if (!response.statusCode.is2xxSuccessful) {
      return RestError.badRequest("Got response from node, but status was not 2xx").toResponseEntity()
    }
    nodes.add(node)
    return ResponseEntity.ok(node)
  }

  class NotifyBody(val url: String)
  class Health()
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
