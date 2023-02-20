package simpleJson.reflection

import arrow.core.Either
import arrow.core.continuations.either
import simpleJson.*
import simpleJson.exceptions.JsonException
import java.io.OutputStream
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

inline fun <reified T : Any> serializeToStream(instance: T, stream: OutputStream, charset: Charset = Charsets.UTF_8): Either<JsonException, Unit> = either.eager {
    JsonWriter.write(serialize(instance, typeOf<T>()).bind(), stream, charset)
}

inline fun <reified T : Any> serializeToString(instance: T): Either<JsonException, String> = either.eager {
    JsonWriter.write(serialize(instance, typeOf<T>()).bind())
}

inline fun <reified T : Any> serializeToNode(instance: T): Either<JsonException, JsonNode> = either.eager {
    serialize(instance, typeOf<T>()).bind()
}

fun <T : Any> serialize(instance: T, kType: KType ): Either<JsonException, JsonNode> = either.eager {
    val kClass = kType.classifier as KClass<T>

    if(kClass.isData) {
        val constructor = kClass.primaryConstructor ?: shift(JsonException("No primary constructor found for $kClass"))
        constructor.isAccessible = true // allow private constructors
        buildObject(kClass, instance).bind()
    }
    else if (arraySupportedTypes.any { it.isSupertypeOf(kClass.starProjectedType) }) {
        buildArray(instance as List<*>, kType.arguments.first().type!!).bind()
    }
    else
        shift(JsonException("$kClass is not a data class or an array type"))
}

private fun <T : Any> buildObject(kClass: KClass<T>, instance: T) : Either<JsonException, JsonObject> = either.eager {
    jObject {
    kClass.memberProperties.forEach {
        it.isAccessible = true
        val value = it.getter.call(instance)

        if (!it.returnType.isMarkedNullable && value == null)
            shift<JsonNode>(JsonException("Null value for non-nullable property ${it.name} in $kClass"))

        it.name += getNode(value, it.returnType).bind()
        }
    }
}

private fun buildArray(value : List<*>, type : KType) : Either<JsonException, JsonNode> = either.eager {
    jArray {
        value.forEach {
            +getNode(it, type).bind()
        }
    }
}

private fun getNode(value : Any?, type: KType) : Either<JsonException, JsonNode> = either.eager {
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