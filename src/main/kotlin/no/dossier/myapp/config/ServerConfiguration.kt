package no.dossier.myapp.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ServerConfiguration(
    @Value("\${server.url}") val url: String
) {
  private val logger = LoggerFactory.getLogger(ServerConfiguration::class.java)

  init {
    logger.info("Server URL: [$url]")
  }
}
