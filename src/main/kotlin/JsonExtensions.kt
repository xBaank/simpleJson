
fun JsonNode.tryGetProperty(key: String): JsonNode? = when (this) {
    is JsonObject -> this[key]
    else -> null
}

fun JsonNode.tryGetArray(key: String): List<JsonNode>? = tryGetProperty(key)?.let {
    when (it) {
        is JsonArray -> it.value
        else -> null
    }
}

fun JsonNode.tryGetString(key: String): String? = tryGetProperty(key)?.let {
    when (it) {
        is JsonString -> it.value
        else -> null
    }
}

fun JsonNode.tryGetNumber(key: String): Number? = tryGetProperty(key)?.let {
    when (it) {
        is JsonNumber -> it.value
        else -> null
    }
}

fun JsonNode.tryGetInt(key: String): Number? = tryGetNumber(key)?.toInt()
fun JsonNode.tryGetDouble(key: String): Number? = tryGetNumber(key)?.toDouble()
fun JsonNode.tryGetFloat(key: String): Number? = tryGetNumber(key)?.toFloat()
fun JsonNode.tryGetLong(key: String): Number? = tryGetNumber(key)?.toLong()

fun JsonNode.tryGetBoolean(key: String): Boolean? = tryGetProperty(key)?.let {
    when (it) {
        is JsonBoolean -> it.value
        else -> null
    }
}

fun JsonNode.tryGetObject(key: String): JsonObject? = tryGetProperty(key)?.let {
    when (it) {
        is JsonObject -> it
        else -> null
    }
}

fun JsonNode.tryGetNull(key: String): JsonNull? = tryGetProperty(key) as JsonNull?

operator fun JsonObject.get(key: String): JsonNode? = value.getOrDefault(key, null)
