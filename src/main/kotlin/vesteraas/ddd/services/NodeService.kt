package vesteraas.ddd.services

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import vesteraas.ddd.data.NodeRepository
import vesteraas.ddd.domain.Health
import vesteraas.ddd.domain.Node
import vesteraas.ddd.domain.NodeState

@Service
class NodeService(
    private val restTemplate: RestTemplate,
    private val nodeRepository: NodeRepository
) {

  fun getNodes(): Set<Node> {
    return nodeRepository.getNodes().filter { it.state != NodeState.DOWN }.toSet()
  }

  @Scheduled(fixedDelayString = "PT1M")
  fun updateNodes() {
    getNodes().forEach {
      val newState = it.state.newState(isHealthy(it))
      if (newState != it.state) {
        nodeRepository.updateNodeState(it, newState)
      }
    }
  }

  fun addNode(node: Node): Boolean {
    return if (isHealthy(node)) {
      nodeRepository.addNode(node)
      true
    } else {
      false
    }
  }

  fun isHealthy(node: Node): Boolean =
      try {
        val response = restTemplate.getForEntity<Health>(node.url + "/health")
        response.statusCode.is2xxSuccessful
      } catch (e: Exception) {
        false
      }
}
