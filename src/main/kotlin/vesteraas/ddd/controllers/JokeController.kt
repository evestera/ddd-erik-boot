package vesteraas.ddd.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class JokeController {

  @GetMapping("/joke")
  fun joke(): Joke = jokes.random()

  private val jokes = setOf(
      Joke("Why do Java developers wear glasses? Because they cannot C#")
  )

  class Joke(val text: String)
}
