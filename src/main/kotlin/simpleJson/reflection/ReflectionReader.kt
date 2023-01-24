package simpleJson.reflection

import arrow.core.Either
import arrow.core.continuations.either
import simpleJson.*
import simpleJson.exceptions.JsonException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType

fun <T : Any> deserialize(json: JsonNode, kClass: KClass<T>): Either<JsonException, T> = either.eager {
    val constructor =
        kClass.primaryConstructor ?: shift(JsonException("No primary constructor found for $kClass"))
    constructor.parameters.forEach {
        println("Parameter: ${it.name} ${it.type}")
    }
    val constructorMap = constructor.parameters.associateWith { kParameter ->
        if (kParameter.name == null)
            shift<JsonException>(JsonException("No name found for parameter $kParameter"))

        val value = getValueFromObject(json, kParameter.type, kParameter.name!!).bind()

        if (value == null && !kParameter.type.isMarkedNullable)
            shift<JsonException>(JsonException("Null value for non-nullable parameter $kParameter"))
        
        if (value != null && !kParameter.type.isSubtypeOf(value::class.starProjectedType))
            shift<JsonException>(JsonException("Value type ${value::class.starProjectedType} is not of type ${kParameter.type}"))
    }

    constructor.callBy(constructorMap)
}

private fun getValueFromObject(json: JsonNode, type: KType, name: String): Either<JsonException, Any?> = either.eager {
    when (val value = json[name].bind()) {
        is JsonString -> value.to_String().bind()
        is JsonBoolean -> value.toBoolean().bind()
        is JsonNumber -> {
            if (type.isSubtypeOf(Int::class.starProjectedType)) value.toInt().bind()
            else if (type.isSubtypeOf(Double::class.starProjectedType)) value.toDouble().bind()
            else if (type.isSubtypeOf(Float::class.starProjectedType)) value.toFloat().bind()
            else if (type.isSubtypeOf(Long::class.starProjectedType)) value.toLong().bind()
            else if (type.isSubtypeOf(Short::class.starProjectedType)) value.toShort().bind()
            else shift(JsonException("Unsupported number type ${type.classifier}"))
        }

        is JsonNull -> value.toNull().bind()
        is JsonArray -> getValuesFromList(value, type.arguments.first().type!!).bind()
        is JsonObject -> deserialize(value, type.classifier as KClass<*>).bind()
    }
}

private fun getValuesFromList(json: JsonArray, type: KType): Either<JsonException, List<Any?>> = either.eager {
    json.map { value ->
        val returnValue = when (value) {
            is JsonString -> value.to_String().bind()
            is JsonBoolean -> value.toBoolean().bind()
            is JsonNumber -> {
                if (type.isSubtypeOf(Int::class.starProjectedType)) value.toInt().bind()
                else if (type.isSubtypeOf(Double::class.starProjectedType)) value.toDouble().bind()
                else if (type.isSubtypeOf(Float::class.starProjectedType)) value.toFloat().bind()
                else if (type.isSubtypeOf(Long::class.starProjectedType)) value.toLong().bind()
                else if (type.isSubtypeOf(Short::class.starProjectedType)) value.toShort().bind()
                else if (type.isSubtypeOf(Byte::class.starProjectedType)) value.toByte().bind()
                else shift(JsonException("Unsupported number type ${type.classifier}"))
            }

            is JsonNull -> value.toNull().bind()
            is JsonArray -> getValuesFromList(value, type.arguments.first().type!!).bind()
            is JsonObject -> deserialize(value, type.classifier as KClass<*>).bind()
        } ?: shift(JsonException("Null value found in list"))

        if (returnValue::class.starProjectedType.isSubtypeOf(type)) returnValue
        else shift(JsonException("Type mismatch: ${returnValue::class.starProjectedType} is not a subtype of $type"))
    }
}


inline fun <reified T : Any> deserialize(json: JsonNode): Either<JsonException, T> =
    deserialize(json, T::class)

inline fun <reified T : Any> deserialize(json: String): Either<JsonException, T> =
    either.eager { deserialize<T>(json.deserialize().bind()).bind() }