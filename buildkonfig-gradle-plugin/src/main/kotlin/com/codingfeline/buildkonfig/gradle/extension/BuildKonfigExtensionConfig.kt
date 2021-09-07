package com.codingfeline.buildkonfig.gradle.extension

import com.codingfeline.buildkonfig.compiler.DEFAULT_KONFIG_OBJECT_NAME
import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.dsl.BuildKonfigDsl
import java.io.Serializable

@BuildKonfigDsl
open class BuildKonfigExtensionConfig() {
    var packageName: String? = null
    var objectName: String = DEFAULT_KONFIG_OBJECT_NAME
    var exposeObjectWithName: String? = null
    internal val appConfigList = mutableListOf(AppDslConfig().apply { name = "" })

    @BuildKonfigDsl
    class AppDslConfig : Serializable {
        var name: String = ""
        internal val defaultConfigs = mutableMapOf<String, DefaultDslConfig>()
        internal val targetConfigs = mutableMapOf<String, TargetDslConfig>()
    }

    @BuildKonfigDsl
    class DefaultDslConfig : Serializable {
        internal val fieldSpecs = mutableMapOf<String, FieldSpec>()
    }

    @BuildKonfigDsl
    class TargetDslConfig : Serializable {
        internal val flavorConfigs = mutableMapOf<String, DefaultDslConfig>()
    }
}