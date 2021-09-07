import com.codingfeline.buildkonfig.gradle.extension.appConfigs
import com.codingfeline.buildkonfig.gradle.extension.buildConfigField
import com.codingfeline.buildkonfig.gradle.extension.buildkonfig
import com.codingfeline.buildkonfig.gradle.extension.defaultConfig
import com.codingfeline.buildkonfig.gradle.extension.target
import com.codingfeline.buildkonfig.gradle.extension.targetConfigs

plugins {

    kotlin("multiplatform")
    id("com.codingfeline.buildkonfig")

}


buildscript {
    repositories {
        // Use 'gradle install' to install latest
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        classpath("com.netcosports:build-konfig:1.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")

    }
}


kotlin {
    jvm()
}


buildkonfig {
    this.packageName = "com.netcosports.test"
    appConfigs("") {

        defaultConfig {
            buildConfigField("myField", com.codingfeline.buildkonfig.compiler.FieldSpec.FieldValue.StringValue("test"))
        }

        targetConfigs() {
            target("jvm") {
                buildConfigField("test", com.codingfeline.buildkonfig.compiler.FieldSpec.FieldValue.StringValue("test"))
            }
        }
    }
}
