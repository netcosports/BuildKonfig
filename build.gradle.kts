buildscript {
    repositories {
        mavenCentral()
        google()
        jcenter()
        gradlePluginPortal()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { url = uri("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
        maven(url = "https://artifactory-blr.netcodev.com/artifactory/libs-release") {
            credentials {
                username = repoUsername
                password = repoPassword
            }
        }
    }

    dependencies {
        classpath(com.codingfeline.buildkonfig.buildsrc.Dependencies.kotlinPlugin)
        classpath(com.codingfeline.buildkonfig.buildsrc.Dependencies.dokkaPlugin)
        classpath(com.codingfeline.buildkonfig.buildsrc.Dependencies.gradleVersionsPlugin)
        classpath(com.codingfeline.buildkonfig.buildsrc.Dependencies.pluginPublishPlugin)
        classpath(com.codingfeline.buildkonfig.buildsrc.Dependencies.mavenPublishPlugin)
    }
}

plugins {
    id("com.github.ben-manes.versions") version com.codingfeline.buildkonfig.buildsrc.Versions.benManesVersionsPlugin
    id("com.jfrog.artifactory") version com.codingfeline.buildkonfig.buildsrc.Versions.jfrogArtifactoryVersion
}


val GROUP: String by project
val VERSION_NAME: String by project

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven(url = "https://artifactory-blr.netcodev.com/artifactory/libs-release") {
            credentials {
                username = repoUsername
                password = repoPassword
            }
        }
    }

    group = GROUP
    version = VERSION_NAME
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}

tasks.wrapper {
    gradleVersion = com.codingfeline.buildkonfig.buildsrc.Versions.gradle
    distributionType = Wrapper.DistributionType.ALL
}
