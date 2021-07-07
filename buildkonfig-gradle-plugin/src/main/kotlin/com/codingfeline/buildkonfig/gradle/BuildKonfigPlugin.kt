package com.codingfeline.buildkonfig.gradle


import com.codingfeline.buildkonfig.compiler.PlatformType
import com.codingfeline.buildkonfig.compiler.TargetName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import java.io.File

@Suppress("unused")
open class BuildKonfigPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        var isMultiplatform = false
        target.plugins.all { p ->
            if (p is KotlinMultiplatformPluginWrapper) {
                isMultiplatform = true
            }
        }

        val extension = target.extensions.create("buildkonfig", BuildKonfigExtension::class.java, target)

        target.afterEvaluate {
            if (!isMultiplatform) {
                throw IllegalStateException(
                    "BuildKonfig Gradle plugin applied in project '${target.path}' " +
                            "but no supported Kotlin multiplatform plugin was found"
                )
            }

            configure(target, extension)
        }
    }


    private fun configure(project: Project, extension: BuildKonfigExtension) {
        val outputDirectory = File(project.buildDir, "buildkonfig")
        val commonOutputDirectory = File(outputDirectory, "commonMain").also { it.mkdirs() }

        val mppExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val targets = mppExtension.targets
        val sourceSets = mppExtension.sourceSets

        val outputDirectoryMap = mutableMapOf<TargetName, File>()

        sourceSets.getByName("commonMain").kotlin
            .srcDirs(commonOutputDirectory.toRelativeString(project.projectDir))

        val createdSourceSet = mutableSetOf<String>()
        targets.filter { it.name != "metadata" }.forEach { target ->
            val name = "${target.name}Main"
            val sourceSetMain = sourceSets.getByName(name)

            val dependentSourceSet = sourceSetMain.dependsOn.filter { it.name != "commonMain" }
            val buildSourceSet = if (dependentSourceSet.isNotEmpty()) {
                dependentSourceSet
            } else {
                setOf(sourceSetMain)
            }


            buildSourceSet.forEach { sourceSet ->
                val name = sourceSet.name

                if (!createdSourceSet.contains(name)) {
                    val outDirMain = File(outputDirectory, name).also { it.mkdirs() }

                    sourceSet.kotlin.srcDirs(outDirMain.toRelativeString(project.projectDir))

                    outputDirectoryMap[TargetName(name.replace("Main", ""), target.platformType.toKgqlPlatformType())] =
                        outDirMain
                    createdSourceSet.add(name)
                }
            }
        }

        project.afterEvaluate { p ->

            val task = p.tasks.register("generateBuildKonfig", BuildKonfigTask::class.java) {
                it.packageName = requireNotNull(extension.packageName) { "packageName must be provided" }
                require(extension.objectName.isNotBlank()) { "objectName must not be blank" }

                var objectName = extension.objectName
                var exposeObject = false
                extension.exposeObjectWithName.takeIf { name -> !name.isNullOrBlank() }
                    ?.also { name ->
                        objectName = name
                        exposeObject = true
                    }

                it.objectName = objectName
                it.exposeObject = exposeObject
                it.commonOutputDirectory = commonOutputDirectory
                it.outputDirectories = outputDirectoryMap
                it.extension = extension

                it.group = "buildkonfig"
                it.description = "generate BuildKonfig"
            }

            p.extensions.getByType(KotlinMultiplatformExtension::class.java).targets.forEach { target ->
                target.compilations.forEach { compilationUnit ->
                    when (compilationUnit) {
                        is KotlinNativeCompilation -> {

                            p.tasks.named(compilationUnit.compileAllTaskName).configure { it.dependsOn(task) }
                            p.tasks.named(compilationUnit.compileKotlinTaskName).configure { it.dependsOn(task) }

                            compilationUnit.target.binaries.forEach { binary ->
                                p.tasks.named(binary.linkTaskName).configure { it.dependsOn(task) }
                            }
                        }
                        is KotlinJvmAndroidCompilation -> {
                            p.tasks.named(compilationUnit.compileKotlinTaskName)
                                .configure { it.dependsOn(task) }
                        }
                        else -> p.tasks.named(compilationUnit.compileKotlinTaskName).configure { it.dependsOn(task) }
                    }
                }
            }
        }
    }
}

internal fun KotlinPlatformType.toKgqlPlatformType(): PlatformType {
    return when (this) {
        KotlinPlatformType.common -> PlatformType.common
        KotlinPlatformType.jvm -> PlatformType.jvm
        KotlinPlatformType.js -> PlatformType.js
        KotlinPlatformType.androidJvm -> PlatformType.androidJvm
        KotlinPlatformType.native -> PlatformType.native
    }
}