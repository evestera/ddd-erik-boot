package no.dossier.myapp

import no.dossier.myapp.config.ServerConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ServerConfiguration::class)
class BootNodeApplication

fun main(args: Array<String>) {
  runApplication<BootNodeApplication>(*args)
}
