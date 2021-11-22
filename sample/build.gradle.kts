plugins {

    kotlin("multiplatform")
//    id("com.codingfeline.buildkonfig")

}


buildscript {
    repositories {
        // Use 'gradle install' to install latest
        mavenLocal()
        mavenCentral()
    }

    dependencies {

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")

    }
}


kotlin {
    jvm()
    ios {
        binaries {
            framework {
                baseName = "sample"
            }
        }
    }
}

//
//buildkonfig {
//    this.packageName = "com.netcosports.test"
//    appConfig("") {
//
//        defaultConfig {
//            buildConfigField("myField", com.codingfeline.buildkonfig.compiler.FieldSpec.FieldValue.StringValue("test"))
//        }
//
//        targetConfigs() {
//            target("jvm") {
//                buildConfigField("test", com.codingfeline.buildkonfig.compiler.FieldSpec.FieldValue.StringValue("test"))
//            }
//        }
//    }
//}
