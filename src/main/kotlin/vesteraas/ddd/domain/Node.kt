package vesteraas.ddd.domain

data class Node(
    val url: String,
    val state: NodeState = NodeState.UP
) {
  override fun equals(other: Any?): Boolean =
      url == (other as? Node)?.url

  override fun hashCode(): Int =
      url.hashCode()
}

enum class NodeState {
  UP,
  FAILING,
  DOWN;

  fun newState(healthy: Boolean): NodeState =
      if (healthy) {
        UP
      } else {
        if (this == UP) {
          FAILING
        } else {
          DOWN
        }
      }
}
