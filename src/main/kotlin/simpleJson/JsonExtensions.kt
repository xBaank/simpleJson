package simpleJson

import arrow.core.*
import simpleJson.exceptions.JsonException


operator fun JsonNode.get(key: String): Either<JsonException, JsonNode> = when (val node = this) {
    is JsonObject -> node[key]?.right() ?: JsonException("Property $key not found").left()
    else -> JsonException("Property $key is a ${this::class.simpleName}, not a ${JsonObject::class.simpleName}").left()
}

operator fun JsonNode.get(index: Int): Either<JsonException, JsonNode> = when (val node = this) {
    is JsonArray -> node.value.getOrNull(index)?.right() ?: JsonException("Property at index $index not found").left()
    else -> JsonException("Property at index $index is a ${this::class.simpleName}, not a ${JsonArray::class.simpleName}").left()
}

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

fun JsonNode.toNumber(): Either<JsonException, Number> = when (this) {
    is JsonNumber -> value.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonNumber::class.simpleName}").left()
}

fun JsonNode.toInt(): Either<JsonException, Int> = toNumber().map(Number::toInt)
fun JsonNode.toDouble(): Either<JsonException, Double> = toNumber().map(Number::toDouble)
fun JsonNode.toFloat(): Either<JsonException, Float> = toNumber().map(Number::toFloat)
fun JsonNode.toLong(): Either<JsonException, Long> = toNumber().map(Number::toLong)

fun JsonNode.to_String(): Either<JsonException, String> = when (this) {
    is JsonString -> value.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonString::class.simpleName}").left()
}

fun JsonNode.toBoolean(): Either<JsonException, Boolean> = when (this) {
    is JsonBoolean -> value.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonBoolean::class.simpleName}").left()
}

fun JsonNode.toNull(): Either<JsonException, Nothing?> = when (this) {
    is JsonNull -> null.right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonNull::class.simpleName}").left()
}

fun JsonNode.toArray() = when (this) {
    is JsonArray -> right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonArray::class.simpleName}").left()
}

fun JsonNode.toObject() = when (this) {
    is JsonObject -> right()
    else -> JsonException("${this::class.simpleName} is not a ${JsonObject::class.simpleName}").left()
}


fun Number.toJson(): JsonNumber = JsonNumber(this)
fun String.toJson(): JsonString = JsonString(this)
fun Boolean.toJson(): JsonBoolean = JsonBoolean(this)

@Suppress("UnusedReceiverParameter")
fun Nothing?.toJson(): JsonNull = JsonNull
fun List<JsonNode>.toJson(): JsonArray = JsonArray(this)
fun Map<String, JsonNode>.toJson(): JsonObject = JsonObject(this)


fun JsonNode.serialize(): String = JsonWriter.write(this)
fun JsonNode.serializePretty(): String = PrettyJsonWriter.write(this)
fun String.deserialize(): Either<JsonException, JsonNode> = JsonReader.read(this)

fun JsonWriter.prettyPrint(indent: String = "  ") = PrettyJsonWriter(this, indent)
