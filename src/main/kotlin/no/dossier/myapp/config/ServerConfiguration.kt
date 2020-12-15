package no.dossier.myapp.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("server")
@ConstructorBinding
class ServerConfiguration(
    val url: String
) {
  private val logger = LoggerFactory.getLogger(ServerConfiguration::class.java)

  init {
    logger.info("Server URL: [$url]")
  }
}
