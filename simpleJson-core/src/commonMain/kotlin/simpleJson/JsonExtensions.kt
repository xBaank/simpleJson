package simpleJson

import arrow.core.*
import simpleJson.exceptions.JsonException
import kotlin.jvm.JvmName


/**
 * Get a property from a [JsonObject] or return a [JsonException] if the property is not found
 */
operator fun JsonNode.get(key: String): Either<JsonException, JsonNode> = when (val node = this) {
    is JsonObject -> node.value[key]?.right() ?: JsonException("Property $key not found").left()
    else -> JsonException("Property $key is a ${this::class.simpleName}, not a ${JsonObject::class.simpleName}").left()
}

/**
 * Get a property from a [JsonArray] or return a [JsonException] if the property is not found
 */
operator fun JsonNode.get(index: Int): Either<JsonException, JsonNode> = when (val node = this) {
    is JsonArray -> node.value.getOrNull(index)?.right() ?: JsonException("Property at index $index not found").left()
    else -> JsonException("Property at index $index is a ${this::class.simpleName}, not a ${JsonArray::class.simpleName}").left()
}

/**
 * Set a property in a [JsonObject] or return a [JsonException] if the node is not a [JsonObject]
 */
operator fun JsonNode.set(key: String, value : JsonNode): Either<JsonException, Unit> = when (val node = this) {
    is JsonObject -> {
        node.value[key] = value
        Unit.right()
    }
    else -> JsonException("This node is not a JsonObject").left()
}

/**
 * Set a property in a [JsonArray] or return a [JsonException] if the node is not a [JsonArray]
 */
operator fun JsonNode.set(index: Int, value : JsonNode): Either<JsonException, Unit> = when (val node = this) {
    is JsonArray -> {
        node.value[index] = value
        Unit.right()
    }
    else -> JsonException("This node is not a JsonArray").left()
}

/**
 * Set a property in a [JsonObject] or return a [JsonException] if the node is not a [JsonObject]
 */
operator fun JsonNode.set(key: String, value : String): Either<JsonException, Unit> = set(key, JsonString(value))

/**
 * Set a property in a [JsonObject] or return a [JsonException] if the node is not a [JsonObject]
 */
operator fun JsonNode.set(key: String, value : Number): Either<JsonException, Unit> = set(key, JsonNumber(value))

/**
 * Set a property in a [JsonObject] or return a [JsonException] if the node is not a [JsonObject]
 */
operator fun JsonNode.set(key: String, value : Boolean): Either<JsonException, Unit> = set(key, JsonBoolean(value))

/**
 * Set a property in a [JsonObject] or return a [JsonException] if the node is not a [JsonObject]
 */
@Suppress("UNUSED_PARAMETER")
operator fun JsonNode.set(key: String, value : Nothing?): Either<JsonException, Unit> = set(key, JsonNull)

/**
 * Set a property in a [JsonArray] or return a [JsonException] if the node is not a [JsonArray]
 */
operator fun JsonNode.set(index: Int, value : String): Either<JsonException, Unit> = set(index, JsonString(value))

/**
 * Set a property in a [JsonArray] or return a [JsonException] if the node is not a [JsonArray]
 */
operator fun JsonNode.set(index: Int, value : Number): Either<JsonException, Unit> = set(index, JsonNumber(value))

/**
 * Set a property in a [JsonArray] or return a [JsonException] if the node is not a [JsonArray]
 */
operator fun JsonNode.set(index: Int, value : Boolean): Either<JsonException, Unit> = set(index, JsonBoolean(value))

/**
 * Set a property in a [JsonArray] or return a [JsonException] if the node is not a [JsonArray]
 */
@Suppress("UNUSED_PARAMETER")
operator fun JsonNode.set(index: Int, value : Nothing?): Either<JsonException, Unit> = set(index, JsonNull)

/**
 * Get a property as a [JsonObject] or return a [JsonException] if the property is not found or is not a [JsonObject]
 */
fun JsonNode.getObject(key: String): Either<JsonException, JsonObject> = get(key).flatMap {
    when (it) {
        is JsonObject -> it.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonObject::class.simpleName}").left()
    }
}

/**
 * Get a property as a [JsonArray] or return a [JsonException] if the property is not found or is not a [JsonArray]
 */
fun JsonNode.getArray(key: String): Either<JsonException, JsonArray> = get(key).flatMap {
    when (it) {
        is JsonArray -> it.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonArray::class.simpleName}").left()
    }
}

/**
 * Get a property as a [String] or return a [JsonException] if the property is not found or is not a [JsonString]
 */
fun JsonNode.getString(key: String): Either<JsonException, String> = get(key).flatMap {
    when (it) {
        is JsonString -> it.value.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonString::class.simpleName}").left()
    }
}

/**
 * Get a property as a [Number] or return a [JsonException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getNumber(key: String): Either<JsonException, Number> = get(key).flatMap {
    when (it) {
        is JsonNumber -> it.value.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonNumber::class.simpleName}").left()
    }
}

/**
 * Get a property as a [Int] or return a [JsonException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getInt(key: String): Either<JsonException, Int> = getNumber(key).map(Number::toInt)

/**
 * Get a property as a [Double] or return a [JsonException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getDouble(key: String): Either<JsonException, Double> = getNumber(key).map(Number::toDouble)

/**
 * Get a property as a [Float] or return a [JsonException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getFloat(key: String): Either<JsonException, Float> = getNumber(key).map(Number::toFloat)

/**
 * Get a property as a [Long] or return a [JsonException] if the property is not found or is not a [JsonNumber]
 */
fun JsonNode.getLong(key: String): Either<JsonException, Long> = getNumber(key).map(Number::toLong)

/**
 * Get a property as a [Boolean] or return a [JsonException] if the property is not found or is not a [JsonBoolean]
 */
fun JsonNode.getBoolean(key: String): Either<JsonException, Boolean> = get(key).flatMap {
    when (it) {
        is JsonBoolean -> it.value.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonBoolean::class.simpleName}").left()
    }
}

/**
 * Get a property as a [Nothing] (null) or return a [JsonException] if the property is not found or is not a [JsonNull]
 */
fun JsonNode.getNull(key: String): Either<JsonException, Nothing?> = get(key).flatMap {
    when (it) {
        is JsonNull -> null.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonNull::class.simpleName}").left()
    }
}

/**
 * Cast a [JsonNode] to a [Number] or return a [JsonException] if the node is not a [JsonNumber]
 */
fun JsonNode.asNumber(): Either<JsonException, Number> = when (this) {
    is JsonNumber -> value.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonNumber::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [Int] or return a [JsonException] if the node is not a [JsonNumber]
 */
fun JsonNode.asInt(): Either<JsonException, Int> = asNumber().map(Number::toInt)

/**
 * Cast a [JsonNode] to a [Double] or return a [JsonException] if the node is not a [JsonNumber]
 */
fun JsonNode.asDouble(): Either<JsonException, Double> = asNumber().map(Number::toDouble)

/**
 * Cast a [JsonNode] to a [Float] or return a [JsonException] if the node is not a [JsonNumber]
 */
fun JsonNode.asFloat(): Either<JsonException, Float> = asNumber().map(Number::toFloat)

/**
 * Cast a [JsonNode] to a [Long] or return a [JsonException] if the node is not a [JsonNumber]
 */
fun JsonNode.asLong(): Either<JsonException, Long> = asNumber().map(Number::toLong)

/**
 * Cast a [JsonNode] to a [Short] or return a [JsonException] if the node is not a [JsonNumber]
 */
fun JsonNode.asShort(): Either<JsonException, Short> = asNumber().map(Number::toShort)

/**
 * Cast a [JsonNode] to a [Byte] or return a [JsonException] if the node is not a [JsonNumber]
 */
fun JsonNode.asByte(): Either<JsonException, Byte> = asNumber().map(Number::toByte)

/**
 * Cast a [JsonNode] to a [String] or return a [JsonException] if the node is not a [JsonString]
 */
fun JsonNode.asString(): Either<JsonException, String> = when (this) {
    is JsonString -> value.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonString::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [Boolean] or return a [JsonException] if the node is not a [JsonBoolean]
 */
fun JsonNode.asBoolean(): Either<JsonException, Boolean> = when (this) {
    is JsonBoolean -> value.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonBoolean::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [Nothing] (null) or return a [JsonException] if the node is not a [JsonNull]
 */
fun JsonNode.asNull(): Either<JsonException, Nothing?> = when (this) {
    is JsonNull -> null.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonNull::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [JsonArray] or return a [JsonException] if the node is not a [JsonArray]
 */
fun JsonNode.asArray() = when (this) {
    is JsonArray -> right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonArray::class.simpleName}").left()
}

/**
 * Cast a [JsonNode] to a [JsonObject] or return a [JsonException] if the node is not a [JsonObject]
 */
fun JsonNode.asObject() = when (this) {
    is JsonObject -> right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonObject::class.simpleName}").left()
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
fun JsonNode.serialize(): String = JsonWriter.write(this)

/**
 * Serialize a [JsonNode] to a [String] with pretty printing
 */
fun JsonNode.serializePretty(): String = PrettyJsonWriter.write(this)

/**
 * Deserialize a [String] to a [JsonNode] or return a [JsonException] if the string is not valid JSON
 */
fun String.deserialize(): Either<JsonException, JsonNode> = JsonReader.read(this)

/**
 * Create a [JsonWriter] that pretty prints the JSON with the given [indent]
 */

fun JsonWriter.prettyPrint(indent: String = "  ") = PrettyJsonWriter(this, indent)
