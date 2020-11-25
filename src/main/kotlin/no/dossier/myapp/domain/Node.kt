package no.dossier.myapp.domain

data class Node(
    val url: String
) {
  override fun equals(other: Any?): Boolean =
      url == (other as? Node)?.url

  override fun hashCode(): Int =
      url.hashCode()
}
