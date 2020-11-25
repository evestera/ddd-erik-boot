package no.dossier.myapp.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
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
