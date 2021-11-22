import com.codingfeline.buildkonfig.buildsrc.Dependencies
import com.codingfeline.buildkonfig.buildsrc.Versions
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
    id("maven-publish")
    id("com.jfrog.artifactory")
}

val libraryGroupId = "com.netcosports"
val libraryVersion = "1.0.5"
val artifactId = "build-konfig"

group = libraryGroupId
version = libraryVersion

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


gradlePlugin {
    plugins {
        create("buildKonfig") {
            id = "com.codingfeline.buildkonfig"
            implementationClass = "com.codingfeline.buildkonfig.gradle.BuildKonfigPlugin"
        }
    }
}


val fixtureClasspath by configurations.creating

// Append any extra dependencies to the test fixtures via a custom configuration classpath. This
// allows us to apply additional plugins in a fixture while still leveraging dependency resolution
// and de-duplication semantics.
tasks.pluginUnderTestMetadata {
    pluginClasspath.from(fixtureClasspath)
}

dependencies {
    implementation(project(":buildkonfig-compiler"))
//    implementation(kotlin("stdlib-jdk8", Versions.kotlin))

    compileOnly(gradleApi())
    implementation(kotlin("gradle-plugin", Versions.kotlin))

    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.truth)

    fixtureClasspath(kotlin("gradle-plugin", Versions.kotlin))
    fixtureClasspath(Dependencies.androidPlugin)
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = Versions.jvmTarget
}
tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = Versions.jvmTarget
}


val jar by tasks.getting(Jar::class) {

    from(
        configurations.runtimeClasspath.get().mapNotNull {
            if (it.name.contains("buildkonfig-compiler")) {
                println("ziptree")
                zipTree(it)
            } else {
                null
            }
        }
    )
}

publishing {
    publications {

        create<MavenPublication>("jar") {
            groupId = libraryGroupId
            version = libraryVersion
            artifactId = "build-konfig"

            // Tell maven to prepare the generated "*.aar" file for publishing
            artifact("$buildDir/libs/${project.getName()}-${libraryVersion}.jar")

            val result = configurations.getByName("implementation").allDependencies.flatMap { dependency ->
                if (dependency.name == "buildkonfig-compiler") {
                    (dependency as? DefaultProjectDependency)?.dependencyProject?.configurations?.getByName(
                        "implementation"
                    )?.allDependencies.orEmpty()
                } else {
                    listOf(dependency)
                }
            }.distinctBy { it.name }

            pom.withXml {
                val dependencies = asNode().appendNode("dependencies")

                result
                    .filter {
                        it.group.isNullOrEmpty().not()
                    }
                    .forEach {
                        println(it.name)
                        val dependencyNode = dependencies.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }
            }
        }
    }
}



artifactory {
    setContextUrl("https://artifactory-blr.netcodev.com/artifactory")
    publish(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
        repository(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.DoubleDelegateWrapper> {
            setProperty("repoKey", "libs-release-local")
            setProperty("username", repoUsername)
            setProperty("password", repoPassword)
        })
        defaults(delegateClosureOf<groovy.lang.GroovyObject> {
            invokeMethod(
                "publications", "jar"
            )
            invokeMethod(
                "publishArtifacts", true
            )
            invokeMethod(
                "publishPom", true
            )
        })
    })
}
