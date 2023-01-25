package simpleJson.reflection

import arrow.core.Either
import arrow.core.continuations.either
import simpleJson.JsonNode
import simpleJson.JsonWriter
import simpleJson.exceptions.JsonException
import simpleJson.jObject
import simpleJson.toJson
import java.io.OutputStream
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

inline fun <reified T : Any> serialize(instance: T, stream: OutputStream, charset: Charset = Charsets.UTF_8): Either<JsonException, Unit> = either.eager {
    JsonWriter.write(serialize(instance, T::class).bind(),stream, charset)
}

inline fun <reified T : Any> serialize(instance: T): Either<JsonException, String> = either.eager {
    JsonWriter.write(serialize(instance, T::class).bind())
}

fun <T : Any> serialize(instance: T, kClass: KClass<T>, ): Either<JsonException, JsonNode> = either.eager {
    if(kClass.isData) {
        val constructor = kClass.primaryConstructor ?: shift(JsonException("No primary constructor found for $kClass"))
        constructor.isAccessible = true // allow private constructors

        return@eager jObject {
            kClass.memberProperties.forEach {
                it.isAccessible = true
                val value = it.getter.call(instance)

                if(!it.returnType.isMarkedNullable && value == null)
                    shift<JsonException>(JsonException("Null value for non-nullable property ${it.name} in $kClass"))

                it.name +=  getNode(value).bind()
            }
        }

    }
    else
        shift(JsonException("$kClass is not a data class"))
}

private fun getNode(any : Any?) : Either<JsonException, JsonNode> = either.eager {
    when (any) {
        is String -> any.toJson()
        is Boolean -> any.toJson()
        is Number -> any.toJson()
        null -> any.toJson()
        else -> shift(JsonException("Unsupported type ${any::class}"))
    }
}