package no.dossier.myapp.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class JokeController {

  private val jokes = setOf(
      Joke("Dette er en vits...")
  )

  @GetMapping("/joke")
  fun joke(): Joke {
    return jokes.random()
  }

  class Joke(val text: String)
}
