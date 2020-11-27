package no.dossier.myapp.services

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwk.SigningKeyNotFoundException
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.RSAKeyProvider
import org.springframework.stereotype.Service
import java.net.URL
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.*

@Service
class TokenService {

  val tokenKeyId = Random().nextInt().toString()

  private val tokenPublicKey: RSAPublicKey
  private val tokenPrivateKey: RSAPrivateKey

  init {
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator.initialize(2048)
    val tokenKeyPair = keyPairGenerator.generateKeyPair()
    tokenPublicKey = tokenKeyPair.public as RSAPublicKey
    tokenPrivateKey = tokenKeyPair.private as RSAPrivateKey
  }

  private val signingKeyProvider = object : RSAKeyProvider {
    override fun getPrivateKeyId(): String = tokenKeyId

    override fun getPrivateKey(): RSAPrivateKey = tokenPrivateKey

    override fun getPublicKeyById(keyId: String?): RSAPublicKey {
      if (keyId == tokenKeyId) {
        return tokenPublicKey
      }
      throw SigningKeyNotFoundException("No key with ID $keyId found", null)
    }
  }

  private val signingAlgorithm = Algorithm.RSA256(signingKeyProvider)

  fun getToken(fromUrl: String, toUrl: String): String {
    return JWT.create()
        .withIssuer(fromUrl)
        .withAudience(toUrl)
        .withIssuedAt(Date.from(Instant.now()))
        .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
        .sign(signingAlgorithm)
  }

  fun getJwks(): Jwks {
    val publicKey = tokenPublicKey
    return Jwks(
        keys = listOf(
            Jwk(
                alg = "RS256",
                kty = publicKey.algorithm,
                use = "sig",
                kid = tokenKeyId,
                e = Base64.getUrlEncoder().encodeToString(publicKey.publicExponent.toByteArray()),
                n = Base64.getUrlEncoder().encodeToString(publicKey.modulus.toByteArray()),
                x5c = null,
                x5t = null
            )
        )
    )
  }

  class PublicKeyFromJwksProvider(private val jwksUri: String) : RSAKeyProvider {
    override fun getPrivateKeyId(): String? = null

    override fun getPrivateKey(): RSAPrivateKey? = null

    override fun getPublicKeyById(keyId: String?): RSAPublicKey =
        JwkProviderBuilder(URL(jwksUri)).build().get(keyId).publicKey as RSAPublicKey
  }

  // throws JWTVerificationException on failure
  fun validateToken(jwsString: String, selfUrl: String): DecodedJWT {
    val issuer = JWT.decode(jwsString).issuer

    return JWT.require(Algorithm.RSA256(PublicKeyFromJwksProvider("$issuer/.well-known/jwks.json")))
        .withIssuer(issuer)
        .withAudience(selfUrl)
        .build()
        .verify(jwsString)
  }
}

data class Jwks(
    val keys: List<Jwk>
)

data class Jwk(
    val alg: String?,
    val kty: String,
    val use: String?,
    val x5c: List<String>?,
    val e: String?,
    val n: String?,
    val kid: String?,
    val x5t: String?
)
