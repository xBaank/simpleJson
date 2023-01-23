package simpleJson.reflection

import arrow.core.getOrElse
import simpleJson.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType

fun <T : Any> deserialize(json: JsonNode, Kclass: KClass<T>) : T {
    val constructor = Kclass.primaryConstructor ?: throw IllegalArgumentException("No primary constructor found for ${Kclass}")
    constructor.parameters.forEach {
        println("Parameter: ${it.name} ${it.type}")
    }
    val constructorMap = constructor.parameters.associateWith { kParameter ->
        if(kParameter.name == null) throw IllegalArgumentException("No name found for parameter ${kParameter}")

        when(val value = json[kParameter.name!!].getOrElse { throw it }) {
            is JsonString-> value.to_String().getOrElse { throw it }
            is JsonBoolean -> value.toBoolean().getOrElse { throw it }
            is JsonNumber -> {
                if(kParameter.type.isSubtypeOf(Int::class.starProjectedType)) value.toInt().getOrElse { throw it }
                else if(kParameter.type.isSubtypeOf(Double::class.starProjectedType)) value.toDouble().getOrElse { throw it }
                else if(kParameter.type.isSubtypeOf(Float::class.starProjectedType)) value.toFloat().getOrElse { throw it }
                else if(kParameter.type.isSubtypeOf(Long::class.starProjectedType)) value.toLong().getOrElse { throw it }
                else throw IllegalArgumentException("Unsupported number type ${kParameter.type.classifier}")
            }
            is JsonNull -> null
            is JsonArray -> value
            is JsonObject -> deserialize(value, kParameter.type.classifier as KClass<*>)
        }
    }

    return constructor.callBy(constructorMap)
}
inline fun <reified T : Any> deserialize(json: JsonNode): T = deserialize(json, T::class)
inline fun <reified T : Any> deserialize(json: String): T = deserialize(json.deserialize().getOrElse { throw it })