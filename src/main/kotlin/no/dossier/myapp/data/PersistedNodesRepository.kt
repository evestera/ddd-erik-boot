package no.dossier.myapp.data

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class PersistedNodesRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val objectMapper: ObjectMapper
) {

  private val setRowMapper: RowMapper<Set<String>> = RowMapper { rs, _ ->
    objectMapper.readValue(rs.getString("nodes"), object : TypeReference<Set<String>>() {})
  }

  fun getPersistedNodes(client: String): Set<String> {
    val res = jdbcTemplate.query("select nodes from persisted_nodes where client = ?", setRowMapper, client)
    return res.firstOrNull() ?: emptySet()
  }

  fun putPersistedNodes(client: String, nodes: Set<String>) {
    val nodesString = objectMapper.writeValueAsString(nodes)
    jdbcTemplate.update(
        "insert into persisted_nodes (client, nodes) values (?, ?) on conflict (client) do update set nodes = ?",
        client,
        nodesString,
        nodesString
    )
  }

  fun deletePersistedNodes(client: String) {
    jdbcTemplate.update("delete from persisted_nodes where client = ?", client)
  }

  fun getAllClients(): List<String> {
    return jdbcTemplate.query("select client from persisted_nodes") { rs, _ -> rs.getString("client") }
  }

  fun register(clientUrl: String) {
    val nodesString = objectMapper.writeValueAsString(emptySet<String>())
    jdbcTemplate.update(
        "insert into persisted_nodes (client, nodes) values (?, ?) on conflict (client) do nothing",
        clientUrl,
        nodesString
    )
  }

}
