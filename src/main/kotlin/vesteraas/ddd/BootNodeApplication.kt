package vesteraas.ddd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import vesteraas.ddd.config.ServerConfiguration

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ServerConfiguration::class)
class BootNodeApplication

fun main(args: Array<String>) {
  runApplication<BootNodeApplication>(*args)
}
