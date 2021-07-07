package com.codingfeline.buildkonfig.compiler

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import java.io.Serializable

data class FieldSpec(
    val name: String,
    private val value: FieldValue,
    val isTargetSpecific: Boolean = false
) : Serializable {

    val codeBlock: CodeBlock
        get() = buildCodeBlock {
            when (value) {
                is FieldValue.StringValue -> add("%S", value.value)
                is FieldValue.IntValue -> add("%L", value.value)
                is FieldValue.FloatValue -> add("%L", value.value)
                is FieldValue.LongValue -> add("%L", value.value)
                is FieldValue.BooleanValue -> add("%L", value.value)
                is FieldValue.StringListValue -> {
                    var list = "listOf<String>("
                    value.value.forEach {
                        list += "\"$it\""
                    }
                    list += ")"
                    add(
                        "%L", list
                    )
                }
            }
        }

    sealed class FieldValue : Serializable {

        abstract val typeName: TypeName

        data class StringValue(
            val value: String
        ) : FieldValue() {
            override val typeName: TypeName
                get() = String::class.asTypeName()
        }

        data class IntValue(val value: IntValue) : FieldValue() {
            override val typeName: TypeName
                get() = Int::class.asTypeName()
        }

        data class FloatValue(val value: FloatValue) : FieldValue() {
            override val typeName: TypeName
                get() = Float::class.asTypeName()
        }

        data class LongValue(val value: LongValue) : FieldValue() {
            override val typeName: TypeName
                get() = Long::class.asTypeName()
        }

        data class BooleanValue(val value: BooleanValue) : FieldValue() {
            override val typeName: TypeName
                get() = Boolean::class.asTypeName()
        }

        data class StringListValue(val value: List<String>) : FieldValue() {
            override val typeName: TypeName
                get() = List::class.parameterizedBy(String::class)
        }
    }


    val typeName: TypeName
        get() = value.typeName.copy()

}
