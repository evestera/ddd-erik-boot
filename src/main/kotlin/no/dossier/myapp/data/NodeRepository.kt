package no.dossier.myapp.data

import no.dossier.myapp.domain.Node
import no.dossier.myapp.domain.NodeState
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class NodeRepository(
    private val jdbcTemplate: JdbcTemplate
) {

  fun getNodes(): Set<Node> {
    return jdbcTemplate
        .query("select * from nodes") { rs, _ -> Node(
            url = rs.getString("url"),
            state = NodeState.valueOf(rs.getString("state"))
        ) }
        .toSet()
  }

  fun addNode(node: Node) {
    jdbcTemplate
        .update("insert into nodes (url, state) values (?, ?) on conflict (url) do update set state = 'UP'", node.url, node.state.toString())
  }

  fun updateNodeState(node: Node, newState: NodeState) {
    jdbcTemplate
        .update("update nodes set state = ? where url = ?", newState.toString(), node.url)
  }
}
