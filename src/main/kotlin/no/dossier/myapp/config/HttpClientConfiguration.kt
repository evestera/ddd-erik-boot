package no.dossier.myapp.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestTemplate

@Configuration
class HttpClientConfiguration(
    private val server: ServerConfiguration
) {

  @Bean
  fun restTemplate(): RestTemplate = RestTemplateBuilder()
      .interceptors(OutboundHttpInterceptor(server.url))
      .build()

  class OutboundHttpInterceptor(private val appName: String) : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
      val headers = request.headers
      headers.set("clientId", appName)
      return execution.execute(request, body)
    }
  }

}
