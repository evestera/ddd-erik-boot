package no.dossier.myapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BootNodeApplication

fun main(args: Array<String>) {
  runApplication<BootNodeApplication>(*args)
}
