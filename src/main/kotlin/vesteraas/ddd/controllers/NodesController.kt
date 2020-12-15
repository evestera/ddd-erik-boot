package vesteraas.ddd.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import vesteraas.ddd.domain.Node
import vesteraas.ddd.domain.UrlObject
import vesteraas.ddd.services.NodeService
import vesteraas.ddd.services.normalizeUrl

@RestController
class NodesController(
    private val nodeService: NodeService
) {

  @GetMapping("/nodes")
  fun getNodeUrls(): Set<String> =
      nodeService.getNodes().map { it.url }.toSet()

  @PostMapping("/nodes", "/notify")
  fun postNodes(@RequestBody body: UrlObject): ResponseEntity<String> {
    val url = normalizeUrl(body.url)
    val added = nodeService.addNode(Node(url = url))
    return if (added) {
      ResponseEntity.ok("OK: Node with url [$url] registered")
    } else {
      ResponseEntity.badRequest().body("Bad request: Node with url [$url] not registered. Health check failed.")
    }
  }

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
