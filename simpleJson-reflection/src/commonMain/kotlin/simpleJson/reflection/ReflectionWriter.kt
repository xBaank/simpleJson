package simpleJson.reflection

import arrow.core.Either
import arrow.core.continuations.either
import okio.BufferedSink
import simpleJson.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

/**
 * Serializes an object of type [T] to an output stream
 */
inline fun <reified T : Any> serializeToBuffer(
    instance: T,
    buffer: BufferedSink
): Either<JsonSerializationException, Unit> = either.eager {
    JsonWriter.write(serialize(instance, typeOf<T>()).bind(), buffer)
}

/**
 * Serializes an object of type [T] to a string
 */
inline fun <reified T : Any> serializeToString(instance: T): Either<JsonSerializationException, String> = either.eager {
    JsonWriter.write(serialize(instance, typeOf<T>()).bind())
}

/**
 * Serializes an object of type [T] to a JsonNode
 */
inline fun <reified T : Any> serializeToNode(instance: T): Either<JsonSerializationException, JsonNode> = either.eager {
    serialize(instance, typeOf<T>()).bind()
}

/**
 * Serializes an object of type [T] to a JsonNode
 */
fun <T : Any> serialize(instance: T, kType: KType): Either<JsonSerializationException, JsonNode> = either.eager {
    val kClass = kType.classifier as KClass<T>

    if (kClass.isData) {
        val constructor =
            kClass.primaryConstructor ?: shift(JsonSerializationException("No primary constructor found for $kClass"))
        constructor.isAccessible = true // allow private constructors
        buildObject(kClass, instance).bind()
    } else if (arraySupportedTypes.any { it.isSupertypeOf(kClass.starProjectedType) }) {
        buildArray(instance as List<*>, kType.arguments.first().type!!).bind()
    } else
        shift(JsonSerializationException("$kClass is not a data class or an array type"))
}

private fun <T : Any> buildObject(kClass: KClass<T>, instance: T): Either<JsonSerializationException, JsonObject> =
    either.eager {
        jObject {
            kClass.memberProperties.forEach {
                it.isAccessible = true
                val value = it.getter.call(instance)

                if (!it.returnType.isMarkedNullable && value == null)
                    shift<JsonNode>(JsonSerializationException("Null value for non-nullable property ${it.name} in $kClass"))

                it.findAnnotation<JsonName>()?.name ?: it.name += getNode(value, it.returnType).bind()
            }
        }
    }

private fun buildArray(value: List<*>, type: KType): Either<JsonSerializationException, JsonNode> = either.eager {
    jArray {
        value.forEach {
            +getNode(it, type).bind()
        }
    }
}

private fun getNode(value: Any?, type: KType): Either<JsonSerializationException, JsonNode> = either.eager {
    when (value) {
        is String -> value.asJson()
        is Boolean -> value.asJson()
        is Number -> value.asJson()
        null -> value.asJson()
        is ArrayList<*> -> buildArray(value, type).bind()
        is List<*> -> buildArray(value, type).bind()
        else -> serialize(value, type).bind()
    }
}
