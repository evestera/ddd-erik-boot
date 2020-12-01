package no.dossier.myapp.controllers

import no.dossier.myapp.config.ServerConfiguration
import no.dossier.myapp.data.PersistedNodesRepository
import no.dossier.myapp.domain.UrlObject
import no.dossier.myapp.services.TokenService
import no.dossier.myapp.services.async
import no.dossier.myapp.services.extractBearerAuth
import no.dossier.myapp.services.normalizeUrl
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/secure/persist/nodes")
class SecurePersistNodesController(
    private val tokenService: TokenService,
    private val server: ServerConfiguration,
    private val restTemplate: RestTemplate,
    private val persistedNodesRepository: PersistedNodesRepository
) {

  private val logger = LoggerFactory.getLogger(SecurePersistNodesController::class.java)

  @PostMapping("/unregister")
  fun persistNodesUnregister(request: HttpServletRequest) {
    val token = tokenService.validateToken(request.extractBearerAuth(), server.url)
    val clientUrl = normalizeUrl(token.issuer)
    persistedNodesRepository.deletePersistedNodes(clientUrl)
  }

  @PostMapping("/sync", "/register")
  fun persistNodesForceSync(request: HttpServletRequest) {
    val token = tokenService.validateToken(request.extractBearerAuth(), server.url)
    val clientUrl = normalizeUrl(token.issuer)
    persistedNodesRepository.register(clientUrl)
    async { syncNodes(clientUrl) }
  }

  @Scheduled(initialDelayString = "PT30S", fixedDelayString = "PT5M")
  fun scheduledSyncNodes() {
    logger.info("Starting scheduled task [sync nodes]")
    for (clientUrl in persistedNodesRepository.getAllClients()) {
      try {
        syncNodes(clientUrl)
      } catch (e: Exception) {
        logger.info("[sync nodes] Error when syncing nodes to [$clientUrl]: ${e.message}")
      }
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
    val currentNodes = nodesResponse.body!!.map { normalizeUrl(it) }.toSet()
    val persistedNodes = persistedNodesRepository.getPersistedNodes(clientUrl)
    val union = currentNodes.union(persistedNodes).map { normalizeUrl(it) }.toSet()
    val forgottenNodeUrls = persistedNodes.minus(currentNodes)
    logger.info("[sync nodes][$clientUrl] Forgotten URLs: ${forgottenNodeUrls.size} New URLs: ${union.size - persistedNodes.size}")

    if (union != persistedNodes) {
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
