package no.dossier.myapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BootNodeApplication

fun main(args: Array<String>) {
  runApplication<BootNodeApplication>(*args)
}
