package simpleJson

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.use
import simpleJson.exceptions.JsonException
import simpleJson.exceptions.JsonPropertyNotFoundException
import kotlin.jvm.JvmName


/**
 * Get a property from a [JsonObject] or return a [JsonPropertyNotFoundException] if the property is not found
 */
operator fun JsonNode.get(key: String): Either<JsonPropertyNotFoundException, JsonNode> = when (val node = this) {
    is JsonObject -> node.value[key]?.right() ?: JsonPropertyNotFoundException("Property $key not found").left()
    else -> JsonPropertyNotFoundException("Property $key is a ${this::class.simpleName}, not a ${JsonObject::class.simpleName}").left()
}

/**
 * Get a property from a [JsonArray] or return a [JsonPropertyNotFoundException] if the property is not found
 */
operator fun JsonNode.get(index: Int): Either<JsonPropertyNotFoundException, JsonNode> = when (val node = this) {
    is JsonArray -> node.value.getOrNull(index)?.right()
        ?: JsonPropertyNotFoundException("Property at index $index not found").left()

    else -> JsonPropertyNotFoundException("Property at index $index is a ${this::class.simpleName}, not a ${JsonArray::class.simpleName}").left()
}

/**
 * Set a property in a [JsonObject] or return a [JsonPropertyNotFoundException] if the node is not a [JsonObject]
 */
operator fun JsonNode.set(key: String, value: JsonNode): Either<JsonPropertyNotFoundException, Unit> =
    when (val node = this) {
        is JsonObject -> {
            node.value[key] = value
            Unit.right()
        }

        else -> JsonPropertyNotFoundException("This node is not a JsonObject").left()
    }

/**
 * Set a property in a [JsonArray] or return a [JsonPropertyNotFoundException] if the node is not a [JsonArray]
 */
operator fun JsonNode.set(index: Int, value: JsonNode): Either<JsonPropertyNotFoundException, Unit> =
    when (val node = this) {
        is JsonArray -> {
            node.value[index] = value
            Unit.right()
        }

        else -> JsonPropertyNotFoundException("This node is not a JsonArray").left()
    }

/**
 * Set a property in a [JsonObject] or return a [JsonPropertyNotFoundException] if the node is not a [JsonObject]
 */
operator fun JsonNode.set(key: String, value: String): Either<JsonPropertyNotFoundException, Unit> =
    set(key, JsonString(value))

/**
 * Set a property in a [JsonObject] or return a [JsonPropertyNotFoundException] if the node is not a [JsonObject]
 */
operator fun JsonNode.set(key: String, value: Number): Either<JsonPropertyNotFoundException, Unit> =
    set(key, JsonNumber(value))

/**
 * Set a property in a [JsonObject] or return a [JsonPropertyNotFoundException] if the node is not a [JsonObject]
 */
operator fun JsonNode.set(key: String, value: Boolean): Either<JsonPropertyNotFoundException, Unit> =
    set(key, JsonBoolean(value))

/**
 * Set a property in a [JsonObject] or return a [JsonPropertyNotFoundException] if the node is not a [JsonObject]
 */
@Suppress("UNUSED_PARAMETER")
operator fun JsonNode.set(key: String, value: Nothing?): Either<JsonPropertyNotFoundException, Unit> =
    set(key, JsonNull)

/**
 * Set a property in a [JsonArray] or return a [JsonPropertyNotFoundException] if the node is not a [JsonArray]
 */
operator fun JsonNode.set(index: Int, value: String): Either<JsonPropertyNotFoundException, Unit> =
    set(index, JsonString(value))

/**
 * Set a property in a [JsonArray] or return a [JsonPropertyNotFoundException] if the node is not a [JsonArray]
 */
operator fun JsonNode.set(index: Int, value: Number): Either<JsonPropertyNotFoundException, Unit> =
    set(index, JsonNumber(value))

/**
 * Set a property in a [JsonArray] or return a [JsonPropertyNotFoundException] if the node is not a [JsonArray]
 */
operator fun JsonNode.set(index: Int, value: Boolean): Either<JsonPropertyNotFoundException, Unit> =
    set(index, JsonBoolean(value))

/**
 * Set a property in a [JsonArray] or return a [JsonPropertyNotFoundException] if the node is not a [JsonArray]
 */
@Suppress("UNUSED_PARAMETER")
operator fun JsonNode.set(index: Int, value: Nothing?): Either<JsonPropertyNotFoundException, Unit> =
    set(index, JsonNull)

/**
 * Get a property as a [JsonObject] or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonObject]
 */
fun JsonNode.getObject(key: String): Either<JsonPropertyNotFoundException, JsonObject> = get(key).flatMap {
    when (it) {
        is JsonObject -> it.right()
        else -> JsonPropertyNotFoundException("Property $key is a ${it::class.simpleName}, not a ${JsonObject::class.simpleName}").left()
    }
}

/**
 * Get a property as a [JsonArray] or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonArray]
 */
fun JsonNode.getArray(key: String): Either<JsonPropertyNotFoundException, JsonArray> = get(key).flatMap {
    when (it) {
        is JsonArray -> it.right()
        else -> JsonPropertyNotFoundException("Property $key is a ${it::class.simpleName}, not a ${JsonArray::class.simpleName}").left()
    }
}

/**
 * Get a property as a [String] or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonString]
 */
fun JsonNode.getString(key: String): Either<JsonPropertyNotFoundException, String> = get(key).flatMap {
    when (it) {
        is JsonString -> it.value.right()
        else -> JsonPropertyNotFoundException("Property $key is a ${it::class.simpleName}, not a ${JsonString::class.simpleName}").left()
    }
}

/**
 * Get a property as a [Number] or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getNumber(key: String): Either<JsonPropertyNotFoundException, Number> = get(key).flatMap {
    when (it) {
        is JsonNumber -> it.value.right()
        else -> JsonPropertyNotFoundException("Property $key is a ${it::class.simpleName}, not a ${JsonNumber::class.simpleName}").left()
    }
}

/**
 * Get a property as a [Int] or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getInt(key: String): Either<JsonPropertyNotFoundException, Int> = getNumber(key).map(Number::toInt)

/**
 * Get a property as a [Double] or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getDouble(key: String): Either<JsonPropertyNotFoundException, Double> =
    getNumber(key).map(Number::toDouble)

/**
 * Get a property as a [Float] or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getFloat(key: String): Either<JsonPropertyNotFoundException, Float> = getNumber(key).map(Number::toFloat)

/**
 * Get a property as a [Long] or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getLong(key: String): Either<JsonPropertyNotFoundException, Long> = getNumber(key).map(Number::toLong)

/**
 * Get a property as a [Boolean] or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonBoolean]
 */
fun JsonNode.getBoolean(key: String): Either<JsonPropertyNotFoundException, Boolean> = get(key).flatMap {
    when (it) {
        is JsonBoolean -> it.value.right()
        else -> JsonPropertyNotFoundException("Property $key is a ${it::class.simpleName}, not a ${JsonBoolean::class.simpleName}").left()
    }
}

/**
 * Get a property as a [Nothing] (null) or return a [JsonPropertyNotFoundException] if the property is not found or is not a [JsonNull]
 */
fun JsonNode.getNull(key: String): Either<JsonPropertyNotFoundException, Nothing?> = get(key).flatMap {
    when (it) {
        is JsonNull -> null.right()
        else -> JsonPropertyNotFoundException("Property $key is a ${it::class.simpleName}, not a ${JsonNull::class.simpleName}").left()
    }
}

/**
 * Cast a [JsonNode] to a [Number] or return a [JsonPropertyNotFoundException] if the node is not a [JsonNumber]
 */
fun JsonNode.asNumber(): Either<JsonPropertyNotFoundException, Number> = when (this) {
    is JsonNumber -> value.right()
    else -> JsonPropertyNotFoundException("${this::class.simpleName} is not a ${JsonNumber::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [Int] or return a [JsonPropertyNotFoundException] if the node is not a [JsonNumber]
 */
fun JsonNode.asInt(): Either<JsonPropertyNotFoundException, Int> = asNumber().map(Number::toInt)

/**
 * Cast a [JsonNode] to a [Double] or return a [JsonPropertyNotFoundException] if the node is not a [JsonNumber]
 */
fun JsonNode.asDouble(): Either<JsonPropertyNotFoundException, Double> = asNumber().map(Number::toDouble)

/**
 * Cast a [JsonNode] to a [Float] or return a [JsonPropertyNotFoundException] if the node is not a [JsonNumber]
 */
fun JsonNode.asFloat(): Either<JsonPropertyNotFoundException, Float> = asNumber().map(Number::toFloat)

/**
 * Cast a [JsonNode] to a [Long] or return a [JsonPropertyNotFoundException] if the node is not a [JsonNumber]
 */
fun JsonNode.asLong(): Either<JsonPropertyNotFoundException, Long> = asNumber().map(Number::toLong)

/**
 * Cast a [JsonNode] to a [Short] or return a [JsonPropertyNotFoundException] if the node is not a [JsonNumber]
 */
fun JsonNode.asShort(): Either<JsonPropertyNotFoundException, Short> = asNumber().map(Number::toShort)

/**
 * Cast a [JsonNode] to a [Byte] or return a [JsonPropertyNotFoundException] if the node is not a [JsonNumber]
 */
fun JsonNode.asByte(): Either<JsonPropertyNotFoundException, Byte> = asNumber().map(Number::toByte)

/**
 * Cast a [JsonNode] to a [String] or return a [JsonPropertyNotFoundException] if the node is not a [JsonString]
 */
fun JsonNode.asString(): Either<JsonPropertyNotFoundException, String> = when (this) {
    is JsonString -> value.right()
    else -> JsonPropertyNotFoundException("${this::class.simpleName} is not a ${JsonString::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [Boolean] or return a [JsonPropertyNotFoundException] if the node is not a [JsonBoolean]
 */
fun JsonNode.asBoolean(): Either<JsonPropertyNotFoundException, Boolean> = when (this) {
    is JsonBoolean -> value.right()
    else -> JsonPropertyNotFoundException("${this::class.simpleName} is not a ${JsonBoolean::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [Nothing] (null) or return a [JsonPropertyNotFoundException] if the node is not a [JsonNull]
 */
fun JsonNode.asNull(): Either<JsonPropertyNotFoundException, Nothing?> = when (this) {
    is JsonNull -> null.right()
    else -> JsonPropertyNotFoundException("${this::class.simpleName} is not a ${JsonNull::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [JsonArray] or return a [JsonPropertyNotFoundException] if the node is not a [JsonArray]
 */
fun JsonNode.asArray() = when (this) {
    is JsonArray -> right()
    else -> JsonPropertyNotFoundException("${this::class.simpleName} is not a ${JsonArray::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [JsonObject] or return a [JsonPropertyNotFoundException] if the node is not a [JsonObject]
 */
fun JsonNode.asObject() = when (this) {
    is JsonObject -> right()
    else -> JsonPropertyNotFoundException("${this::class.simpleName} is not a ${JsonObject::class.simpleName}").left()
}

/**
 * Cast a [Number] to a [JsonNumber]
 */
fun Number.asJson(): JsonNumber = JsonNumber(this)

/**
 * Cast a [String] to a [JsonString]
 */
fun String.asJson(): JsonString = JsonString(this)

/**
 * Cast a [Boolean] to a [JsonBoolean]
 */
fun Boolean.asJson(): JsonBoolean = JsonBoolean(this)

/**
 * Cast a [Nothing] (null) to a [JsonNull]
 */
@Suppress("UnusedReceiverParameter")
fun Nothing?.asJson(): JsonNull = JsonNull

/**
 * Cast a [MutableList] to a [JsonArray]
 */
fun MutableList<JsonNode>.asJson(): JsonArray = JsonArray(this)

/**
 * Cast a [MutableMap] to a [JsonObject]
 */
fun MutableMap<String, JsonNode>.asJson(): JsonObject = JsonObject(this)

/**
 * Cast a [List] to a [JsonArray]
 */
@JvmName("toJsonList")
fun List<JsonNode>.asJson(): JsonArray = JsonArray(toMutableList())

/**
 * Cast a [Map] to a [JsonObject]
 */
@JvmName("toJsonMap")
fun Map<String, JsonNode>.asJson(): JsonObject = JsonObject(toMutableMap())

/**
 * Serialize a [JsonNode] to a [String]
 */
fun JsonNode.serialized(): String = Buffer().use {
    JsonWriter(it).write(this)
    return it.readUtf8()
}

/**
 * Serialize a [JsonNode] to a [String] with pretty printing
 */
fun JsonNode.serializedPretty(indent: String = "  "): String = Buffer().use {
    PrettyJsonWriter(JsonWriter(it), indent).write(this)
    return it.readUtf8()
}

/**
 * Serialize a [JsonNode] to a [String]
 */
fun JsonNode.serializeTo(buffer: BufferedSink): Unit = JsonWriter(buffer).write(this)

/**
 * Serialize a [JsonNode] to a [String] with pretty printing and the given [indent]
 */
fun JsonNode.serializePrettyTo(buffer: BufferedSink, indent: String = "  "): Unit =
    PrettyJsonWriter(JsonWriter(buffer), indent).write(this)

/**
 * Deserialize a [String] to a [JsonNode] or return a [JsonException] if the string is not valid JSON
 */
fun String.deserialized(): Either<JsonException, JsonNode> = JsonReader(this).read()

/**
 * Deserialize a [String] to a [JsonNode] or return a [JsonException] if the string is not valid JSON
 */
fun BufferedSource.deserialized(): Either<JsonException, JsonNode> = JsonReader(this).read()
