package vesteraas.ddd.domain

class Widget(
    val selector: String,
    val script: String,
    val description: String? = null,
    val name: String? = null
)
