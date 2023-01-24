package simpleJson.reflection

import arrow.core.Either
import arrow.core.continuations.either
import simpleJson.*
import simpleJson.exceptions.JsonException
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

val simpleSupportedTypes = setOf(
    String::class.createType(nullable = true),
    Int::class.createType(nullable = true),
    Double::class.createType(nullable = true),
    Float::class.createType(nullable = true),
    Long::class.createType(nullable = true),
    Short::class.createType(nullable = true),
    Byte::class.createType(nullable = true),
    Boolean::class.createType(nullable = true),
    Nothing::class.createType(nullable = true)
)
val arraySupportedTypes = setOf(
    List::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true),
    ArrayList::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true),
)
val supportedTypes = simpleSupportedTypes + arraySupportedTypes

inline fun <reified T : Any> deserialize(json: JsonNode): Either<JsonException, T> =
    deserialize(json, T::class)

inline fun <reified T : Any> deserialize(json: String): Either<JsonException, T> =
    either.eager { deserialize<T>(json.deserialize().bind()).bind() }

inline fun <reified T : Any> deserialize(json: InputStream, charset: Charset = Charsets.UTF_8): Either<JsonException, T> =
    either.eager { deserialize<T>(JsonReader.read(json, charset).bind()).bind() }

fun <T : Any> deserialize(json: JsonNode, kClass: KClass<T>): Either<JsonException, T> = either.eager {
    if(kClass.isData) {
        val constructor =
            kClass.primaryConstructor ?: shift(JsonException("No primary constructor found for $kClass"))

        val constructorMap = constructor.parameters.associateWith { kParameter ->
            if (kParameter.name == null)
                shift<JsonException>(JsonException("No name found for parameter $kParameter"))

            val value = getValueFromObject(json, kParameter.type, kParameter.name!!).bind()

            if (value == null && !kParameter.type.isMarkedNullable) //check if type was not nullable at compile time, it can come as null
                shift<JsonException>(JsonException("Null value for non-nullable parameter $kParameter"))

            if (!supportedTypes.any { it.isSupertypeOf(kParameter.type) } && !(kParameter.type.classifier as KClass<*>).isData)
                shift<JsonException>(JsonException("$value is not of type ${kParameter.type} for property ${kParameter.name} in $kClass"))

            value
        }

        constructor.isAccessible = true // allow private constructors
        constructor.callBy(constructorMap)
    }
    else if(arraySupportedTypes.any { it.isSupertypeOf(kClass.starProjectedType) }) {
        @Suppress("UNCHECKED_CAST")
        getValuesFromList(json.toArray().bind(), Any::class.starProjectedType).bind() as T
    }
    else
        shift(JsonException("$kClass is not a data class or an array type"))
}

private fun getValue(json: JsonNode, type: KType): Either<JsonException, Any?> = either.eager {
    when (json) {
        is JsonString -> json.to_String().bind()
        is JsonBoolean -> json.toBoolean().bind()
        is JsonNumber -> {
            if (type.isSubtypeOf(Int::class.starProjectedType)) json.toInt().bind()
            else if (type.isSubtypeOf(Byte::class.starProjectedType)) json.toByte().bind()
            else if (type.isSubtypeOf(Double::class.starProjectedType)) json.toDouble().bind()
            else if (type.isSubtypeOf(Float::class.starProjectedType)) json.toFloat().bind()
            else if (type.isSubtypeOf(Long::class.starProjectedType)) json.toLong().bind()
            else if (type.isSubtypeOf(Short::class.starProjectedType)) json.toShort().bind()
            else json.value //If we are deserializing a list of starProjectedType, we can't know the type at run time, so we default to the JsonNumber value
        }

        is JsonNull -> json.toNull().bind()
        is JsonArray -> {
            val isArrayType = arraySupportedTypes.any { it.isSupertypeOf(type) }
            if (!isArrayType) shift<JsonException>(JsonException("Type mismatch: ${type.classifier} is not a supported array type"))
            getValuesFromList(json, type.arguments.first().type!!).bind()
        }
        is JsonObject -> deserialize(json, type.classifier as KClass<*>).bind()
    }
}


private fun getValueFromObject(json: JsonNode, type: KType, name: String): Either<JsonException, Any?> = either.eager {
  getValue(json[name].bind(), type).bind()
}

private fun getValuesFromList(json: JsonArray, type: KType): Either<JsonException, List<Any?>> = either.eager {
    json.map { value ->
        val returnValue = getValue(value,type).bind() ?: shift(JsonException("Null value found in list"))
        if (returnValue::class.starProjectedType.isSubtypeOf(type)) returnValue
        else shift(JsonException("Type mismatch: ${returnValue::class.starProjectedType} is not a subtype of $type"))
    }
}