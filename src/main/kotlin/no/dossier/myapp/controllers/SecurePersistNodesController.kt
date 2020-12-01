package no.dossier.myapp.controllers

import no.dossier.myapp.config.ServerConfiguration
import no.dossier.myapp.data.PersistedNodesRepository
import no.dossier.myapp.domain.UrlObject
import no.dossier.myapp.services.TokenService
import no.dossier.myapp.services.extractBearerAuth
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest

@RestController("/secure/persist/nodes")
class SecurePersistNodesController(
    private val tokenService: TokenService,
    private val server: ServerConfiguration,
    private val restTemplate: RestTemplate,
    private val persistedNodesRepository: PersistedNodesRepository
) {

  @PostMapping("/unregister")
  fun persistNodesUnregister(request: HttpServletRequest) {
    val token = tokenService.validateToken(request.extractBearerAuth(), server.url)
    persistedNodesRepository.deletePersistedNodes(token.issuer)
  }

  @PostMapping("/sync", "/register")
  fun persistNodesForceSync(request: HttpServletRequest) {
    val token = tokenService.validateToken(request.extractBearerAuth(), server.url)
    syncNodes(token.issuer)
  }

  @Scheduled(fixedRateString = "PT10M")
  fun scheduledSyncNodes() {
    for (clientUrl in persistedNodesRepository.getAllClients()) {
      syncNodes(clientUrl)
    }
  }

  private fun syncNodes(clientUrl: String) {
    val nodesEndpointUrl = "$clientUrl/nodes"
    val nodesResponse = restTemplate.exchange(
        nodesEndpointUrl,
        HttpMethod.GET,
        HttpEntity.EMPTY,
        object : ParameterizedTypeReference<List<String>>() {}
    )
    val currentNodes = nodesResponse.body!!.toSet()
    val persistedNodes = persistedNodesRepository.getPersistedNodes(clientUrl)
    val union = currentNodes.union(persistedNodes)
    val forgottenNodeUrls = persistedNodes.minus(currentNodes)

    if (union != currentNodes) {
      persistedNodesRepository.putPersistedNodes(clientUrl, union)
    }

    for (nodeUrl in forgottenNodeUrls) {
      restTemplate.exchange(
          nodesEndpointUrl,
          HttpMethod.POST,
          HttpEntity(UrlObject(nodeUrl)),
          object : ParameterizedTypeReference<List<String>>() {}
      )
    }
  }
}
