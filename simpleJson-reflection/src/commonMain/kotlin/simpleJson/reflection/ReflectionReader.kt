package simpleJson.reflection

import arrow.core.Either
import arrow.core.continuations.either
import okio.BufferedSource
import simpleJson.*
import simpleJson.exceptions.JsonException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

/**
 * Deserializes a JsonNode to the specified type
 */
inline fun <reified T : Any> deserializeFromNode(json: JsonNode): Either<JsonDeserializationException, T> =
    deserialize(json, typeOf<T>())

/**
 * Deserializes a string to the specified type
 */
inline fun <reified T : Any> deserializeFromString(json: String): Either<JsonDeserializationException, T> =
    either.eager { deserializeFromNode<T>(json.deserialized().mapException().bind()).bind() }

/**
 * Deserializes an input stream to the specified type
 */
inline fun <reified T : Any> deserializeFromBuffer(buffer: BufferedSource): Either<JsonDeserializationException, T> =
    either.eager { deserializeFromNode<T>(buffer.deserialized().mapException().bind()).bind() }

/**
 * Deserializes a JsonNode to the specified type
 */
fun <T : Any> deserialize(json: JsonNode, type: KType): Either<JsonDeserializationException, T> = either.eager {
    val kClass = type.classifier as KClass<T>
    if (kClass.isData) {
        deserializeFromDataClass(json, kClass).bind()
    } else if (arraySupportedTypes.any { it.isSupertypeOf(kClass.starProjectedType) }) {
        @Suppress("UNCHECKED_CAST")
        getValuesFromList<T>(json.asArray().mapException().bind(), type.arguments.first().type!!).bind() as T
    } else
        shift(JsonDeserializationException("$kClass is not a data class or an array type"))
}

private fun <T : Any> deserializeFromDataClass(
    json: JsonNode,
    kClass: KClass<T>
): Either<JsonDeserializationException, T> = either.eager {
    val constructor =
        kClass.primaryConstructor ?: shift(JsonDeserializationException("No primary constructor found for $kClass"))

    val constructorMap = constructor.parameters.associateWith { kParameter ->

        val property = kClass.memberProperties.firstOrNull() { it.name == kParameter.name }
        val name = property?.findAnnotation<JsonName>()?.name ?: kParameter.name

        if (name == null) shift<T>(JsonDeserializationException("No name found for parameter $kParameter in $kClass"))

        val value = getValueFromObject<T>(json, kParameter.type, name!!).bind()

        if (value == null && !kParameter.type.isMarkedNullable) //check if type was not nullable at compile time, it can come as null
            shift<T>(JsonDeserializationException("Null value for non-nullable parameter $kParameter"))

        if (!supportedTypes.any { it.isSupertypeOf(kParameter.type) } && !(kParameter.type.classifier as KClass<*>).isData)
            shift<T>(JsonDeserializationException("$value is not of type ${kParameter.type} for property ${kParameter.name} in $kClass"))

        value
    }

    constructor.isAccessible = true // allow private constructors
    constructor.callBy(constructorMap)
}

private fun <T : Any> getValue(json: JsonNode, type: KType): Either<JsonDeserializationException, Any?> = either.eager {
    when (json) {
        is JsonString -> json.asString().mapException().bind()
        is JsonBoolean -> json.asBoolean().mapException().bind()
        is JsonNumber ->
            if (type.isSubtypeOf(Int::class.starProjectedType)) json.asInt().mapException().bind()
            else if (type.isSubtypeOf(Byte::class.starProjectedType)) json.asByte().mapException().bind()
            else if (type.isSubtypeOf(Double::class.starProjectedType)) json.asDouble().mapException().bind()
            else if (type.isSubtypeOf(Float::class.starProjectedType)) json.asFloat().mapException().bind()
            else if (type.isSubtypeOf(Long::class.starProjectedType)) json.asLong().mapException().bind()
            else if (type.isSubtypeOf(Short::class.starProjectedType)) json.asShort().mapException().bind()
            else json.value //If we are deserializing a list of starProjectedType, we can't know the type at run time, so we default to the JsonNumber value
        is JsonNull -> json.asNull().mapException().bind()
        is JsonArray -> {
            val isArrayType = arraySupportedTypes.any { it.isSupertypeOf(type) }
            if (!isArrayType) shift<JsonException>(JsonDeserializationException("Type mismatch: ${type.classifier} is not a supported array type"))
            getValuesFromList<T>(json, type.arguments.first().type!!).bind()
        }

        is JsonObject -> deserialize<T>(json, type).bind()
    }
}


private fun <T : Any> getValueFromObject(
    json: JsonNode,
    type: KType,
    name: String
): Either<JsonDeserializationException, Any?> = either.eager {
    getValue<T>(json[name].mapException().bind(), type).bind()
}

private fun <T : Any> getValuesFromList(
    json: JsonArray,
    type: KType
): Either<JsonDeserializationException, List<Any?>> = either.eager {
    json.map { value ->
        val returnValue =
            getValue<T>(value, type).bind() ?: shift(JsonDeserializationException("Null value found in list"))
        if (returnValue::class.starProjectedType.isSubtypeOf(type)) returnValue
        else shift(JsonDeserializationException("Type mismatch: ${returnValue::class.starProjectedType} is not a subtype of $type"))
    }
}

fun <T> Either<JsonException, T>.mapException() = mapLeft { JsonDeserializationException(it.message) }