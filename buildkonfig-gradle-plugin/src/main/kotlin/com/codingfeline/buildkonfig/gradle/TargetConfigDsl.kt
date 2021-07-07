package com.codingfeline.buildkonfig.gradle

import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.compiler.TargetConfig
import org.gradle.api.logging.Logger
import java.io.Serializable
import javax.inject.Inject

open class TargetConfigDsl @Inject constructor(
    name: String,
    private val logger: Logger
) : TargetConfig(name), Serializable {

    companion object {
        const val serialVersionUID = 1L
    }

    @Suppress("unused")
    fun buildConfigField(
        name: String,
        value: FieldSpec.FieldValue
    ) {

        val alreadyPresent = fieldSpecs[name]

        if (alreadyPresent != null) {
            logger.info("TargetConfig: buildConfigField '$name' is being replaced: ${alreadyPresent} -> $value")
        }
        fieldSpecs[name] = FieldSpec(name, value)
    }

    fun toTargetConfig(): TargetConfig {
        return TargetConfig(name)
            .also {
                it.flavor = flavor
                it.fieldSpecs.putAll(fieldSpecs)
            }
    }
}
