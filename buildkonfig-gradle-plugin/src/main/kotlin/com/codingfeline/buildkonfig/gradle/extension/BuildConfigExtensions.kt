package com.codingfeline.buildkonfig.gradle.extension

import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.gradle.api.Project


fun Project.buildkonfig(action: BuildKonfigExtensionConfig.() -> Unit) =
    this.extensions.configure("buildkonfig", action)

fun BuildKonfigExtensionConfig.appConfig(
    name: String = "",
    config: BuildKonfigExtensionConfig.AppDslConfig.() -> Unit
) {
    val appConfig = appConfigList.find { it.name == name } ?: BuildKonfigExtensionConfig.AppDslConfig(name)
    appConfig.config()
    appConfigList.add(appConfig)
}

fun BuildKonfigExtensionConfig.AppDslConfig.defaultConfig(
    flavorName: String = "",
    config: BuildKonfigExtensionConfig.DefaultDslConfig.() -> Unit
) {
    val defaultConfig = this.defaultConfigs.getOrDefault(flavorName, BuildKonfigExtensionConfig.DefaultDslConfig())
    defaultConfig.config()
    defaultConfigs[flavorName] = defaultConfig
}

fun BuildKonfigExtensionConfig.AppDslConfig.targetConfigs(
    flavorName: String = "",
    config: BuildKonfigExtensionConfig.TargetDslConfig.() -> Unit
) {
    val targetConfig = this.targetConfigs.getOrDefault(flavorName, BuildKonfigExtensionConfig.TargetDslConfig())
    targetConfig.config()
    targetConfigs[flavorName] = targetConfig
}

fun BuildKonfigExtensionConfig.TargetDslConfig.target(
    name: String,
    config: BuildKonfigExtensionConfig.DefaultDslConfig.() -> Unit
) {
    val defaultConfig = flavorConfigs.getOrDefault(name, BuildKonfigExtensionConfig.DefaultDslConfig())
    defaultConfig.config()
    flavorConfigs[name] = defaultConfig
}

fun BuildKonfigExtensionConfig.DefaultDslConfig.buildConfigField(name: String, value: FieldSpec.FieldValue) {

    val alreadyPresent = fieldSpecs[name]

    if (alreadyPresent != null) {
        println("TargetConfig: buildConfigField '$name' is being replaced: ${alreadyPresent} -> $value")
    }
    fieldSpecs[name] = FieldSpec(name, value)

}