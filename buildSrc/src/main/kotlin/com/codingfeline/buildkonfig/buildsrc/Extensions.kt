import org.gradle.api.Project

val Project.repoUsername: String get() = this.properties["repoUsername"].toString()
val Project.repoPassword: String get() = this.properties["repoPassword"].toString()