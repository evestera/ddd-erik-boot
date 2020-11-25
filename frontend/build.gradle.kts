plugins {
  id("com.github.node-gradle.node") version "2.2.4"
}

node {
  download = true
  version = "12.19.1"
}

tasks.getByName("yarn_build") {
  dependsOn("yarn_install")
  inputs.dir("src")
  inputs.files("package.json", "yarn.lock")
  outputs.dir("build")
  outputs.cacheIf { true }
}
