package simpleJson

import arrow.core.*
import simpleJson.exceptions.JsonException


operator fun JsonNode.get(key: String): Either<JsonException, JsonNode> = when (val node = this) {
    is JsonObject -> node.value[key]?.right() ?: JsonException("Property $key not found").left()
    else -> JsonException("Property $key is a ${this::class.simpleName}, not a ${JsonObject::class.simpleName}").left()
}

operator fun JsonNode.get(index: Int): Either<JsonException, JsonNode> = when (val node = this) {
    is JsonArray -> node.value.getOrNull(index)?.right() ?: JsonException("Property at index $index not found").left()
    else -> JsonException("Property at index $index is a ${this::class.simpleName}, not a ${JsonArray::class.simpleName}").left()
}

operator fun JsonNode.set(key: String, value : JsonNode): Either<JsonException, Unit> = when (val node = this) {
    is JsonObject -> {
        node.value[key] = value
        Unit.right()
    }
    else -> JsonException("This node is not a JsonObject").left()
}

operator fun JsonNode.set(index: Int, value : JsonNode): Either<JsonException, Unit> = when (val node = this) {
    is JsonArray -> {
        node.value[index] = value
        Unit.right()
    }
    else -> JsonException("This node is not a JsonArray").left()
}

operator fun JsonNode.set(key: String, value : String): Either<JsonException, Unit> = set(key, JsonString(value))
operator fun JsonNode.set(key: String, value : Number): Either<JsonException, Unit> = set(key, JsonNumber(value))
operator fun JsonNode.set(key: String, value : Boolean): Either<JsonException, Unit> = set(key, JsonBoolean(value))
operator fun JsonNode.set(key: String, value : Nothing?): Either<JsonException, Unit> = set(key, JsonNull)

operator fun JsonNode.set(index: Int, value : String): Either<JsonException, Unit> = set(index, JsonString(value))
operator fun JsonNode.set(index: Int, value : Number): Either<JsonException, Unit> = set(index, JsonNumber(value))
operator fun JsonNode.set(index: Int, value : Boolean): Either<JsonException, Unit> = set(index, JsonBoolean(value))
operator fun JsonNode.set(index: Int, value : Nothing?): Either<JsonException, Unit> = set(index, JsonNull)

fun JsonNode.getObject(key: String): Either<JsonException, JsonObject> = get(key).flatMap {
    when (it) {
        is JsonObject -> it.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonObject::class.simpleName}").left()
    }
}

fun JsonNode.getArray(key: String): Either<JsonException, JsonArray> = get(key).flatMap {
    when (it) {
        is JsonArray -> it.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonArray::class.simpleName}").left()
    }
}

fun JsonNode.getString(key: String): Either<JsonException, String> = get(key).flatMap {
    when (it) {
        is JsonString -> it.value.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonString::class.simpleName}").left()
    }
}

fun JsonNode.getNumber(key: String): Either<JsonException, Number> = get(key).flatMap {
    when (it) {
        is JsonNumber -> it.value.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonNumber::class.simpleName}").left()
    }
}

fun JsonNode.getInt(key: String): Either<JsonException, Int> = getNumber(key).map(Number::toInt)
fun JsonNode.getDouble(key: String): Either<JsonException, Double> = getNumber(key).map(Number::toDouble)
fun JsonNode.getFloat(key: String): Either<JsonException, Float> = getNumber(key).map(Number::toFloat)
fun JsonNode.getLong(key: String): Either<JsonException, Long> = getNumber(key).map(Number::toLong)


fun JsonNode.getBoolean(key: String): Either<JsonException, Boolean> = get(key).flatMap {
    when (it) {
        is JsonBoolean -> it.value.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonBoolean::class.simpleName}").left()
    }
}

fun JsonNode.getNull(key: String): Either<JsonException, Nothing?> = get(key).flatMap {
    when (it) {
        is JsonNull -> null.right()
        else -> JsonException("Property $key is a ${it::class.simpleName}, not a ${JsonNull::class.simpleName}").left()
    }
}

fun JsonNode.asNumber(): Either<JsonException, Number> = when (this) {
    is JsonNumber -> value.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonNumber::class.simpleName}").left()
}

fun JsonNode.asInt(): Either<JsonException, Int> = asNumber().map(Number::toInt)
fun JsonNode.asDouble(): Either<JsonException, Double> = asNumber().map(Number::toDouble)
fun JsonNode.asFloat(): Either<JsonException, Float> = asNumber().map(Number::toFloat)
fun JsonNode.asLong(): Either<JsonException, Long> = asNumber().map(Number::toLong)
fun JsonNode.asShort(): Either<JsonException, Short> = asNumber().map(Number::toShort)
fun JsonNode.asByte(): Either<JsonException, Byte> = asNumber().map(Number::toByte)

fun JsonNode.asString(): Either<JsonException, String> = when (this) {
    is JsonString -> value.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonString::class.simpleName}").left()
}

fun JsonNode.asBoolean(): Either<JsonException, Boolean> = when (this) {
    is JsonBoolean -> value.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonBoolean::class.simpleName}").left()
}

fun JsonNode.asNull(): Either<JsonException, Nothing?> = when (this) {
    is JsonNull -> null.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonNull::class.simpleName}").left()
}

fun JsonNode.asArray() = when (this) {
    is JsonArray -> right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonArray::class.simpleName}").left()
}

fun JsonNode.asObject() = when (this) {
    is JsonObject -> right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonObject::class.simpleName}").left()
}


fun Number.asJson(): JsonNumber = JsonNumber(this)
fun String.asJson(): JsonString = JsonString(this)
fun Boolean.asJson(): JsonBoolean = JsonBoolean(this)

@Suppress("UnusedReceiverParameter")
fun Nothing?.asJson(): JsonNull = JsonNull
fun MutableList<JsonNode>.asJson(): JsonArray = JsonArray(this)
fun MutableMap<String, JsonNode>.asJson(): JsonObject = JsonObject(this)
@JvmName("toJsonList")
fun List<JsonNode>.asJson(): JsonArray = JsonArray(toMutableList())
@JvmName("toJsonMap")
fun Map<String, JsonNode>.asJson(): JsonObject = JsonObject(toMutableMap())


fun JsonNode.serialize(): String = JsonWriter.write(this)
fun JsonNode.serializePretty(): String = PrettyJsonWriter.write(this)
fun String.deserialize(): Either<JsonException, JsonNode> = JsonReader.read(this)

fun JsonWriter.prettyPrint(indent: String = "  ") = PrettyJsonWriter(this, indent)
