/**
 * Returns the value of the property with the given [key] or null if the node is not a JsonObject.
 */
fun JsonNode.getPropertyOrNull(key: String): JsonNode? = when (this) {
    is JsonObject -> this[key]
    else -> null
}


/**
 * Returns the value at the [index] or null if the value is not found or the node is not a JsonArray
 */
fun JsonNode.getPropertyOrNull(index: Int): JsonNode? = when (this) {
    is JsonArray -> this.value.getOrNull(index)
    else -> null
}


fun JsonNode.getArrayOrNull(key: String): JsonArray? = getPropertyOrNull(key)?.let {
    when (it) {
        is JsonArray -> it
        else -> null
    }
}

fun JsonNode.getStringOrNull(key: String): String? = getPropertyOrNull(key)?.let {
    when (it) {
        is JsonString -> it.value
        else -> null
    }
}

fun JsonNode.getNumberOrNull(key: String): Number? = getPropertyOrNull(key)?.let {
    when (it) {
        is JsonNumber -> it.value
        else -> null
    }
}

fun JsonNode.getIntOrNull(key: String): Number? = getNumberOrNull(key)?.toInt()
fun JsonNode.getDoubleOrNull(key: String): Number? = getNumberOrNull(key)?.toDouble()
fun JsonNode.getFloatOrNull(key: String): Number? = getNumberOrNull(key)?.toFloat()
fun JsonNode.getLongOrNull(key: String): Number? = getNumberOrNull(key)?.toLong()

fun JsonNode.getBooleanOrNull(key: String): Boolean? = getPropertyOrNull(key)?.let {
    when (it) {
        is JsonBoolean -> it.value
        else -> null
    }
}

fun JsonNode.getObjectOrNull(key: String): JsonObject? = getPropertyOrNull(key)?.let {
    when (it) {
        is JsonObject -> it
        else -> null
    }
}

fun JsonNode.getJsonNullOrNull(key: String): JsonNull? = getPropertyOrNull(key) as? JsonNull?

fun JsonNode.toNumberOrNull() = when (this) {
    is JsonNumber -> this.value
    else -> null
}

fun JsonNode.toIntOrNull() = toNumberOrNull()?.toInt()
fun JsonNode.toDoubleOrNull() = toNumberOrNull()?.toDouble()
fun JsonNode.toFloatOrNull() = toNumberOrNull()?.toFloat()
fun JsonNode.toLongOrNull() = toNumberOrNull()?.toLong()

fun JsonNode.toStringOrNull() = when (this) {
    is JsonString -> this.value
    else -> null
}

fun JsonNode.toBooleanOrNull() = when (this) {
    is JsonBoolean -> this.value
    else -> null
}

fun JsonNode.toJsonNullOrNull() = when (this) {
    is JsonNull -> this
    else -> null
}

fun JsonNode.toArrayOrNull() = when (this) {
    is JsonArray -> this
    else -> null
}

fun JsonNode.toObjectOrNull() = when (this) {
    is JsonObject -> this
    else -> null
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
fun String.deserialize(): JsonNode = JsonReader.read(this)
fun String.deserializeOrNull(): JsonNode? = JsonReader.readOrNull(this)
