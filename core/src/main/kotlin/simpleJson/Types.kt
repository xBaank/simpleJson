package simpleJson

/**
 * Base type to represent a JSON node.
 */
sealed interface JsonNode

/**
 * Represents a JSON array.
 */
@JvmInline
value class JsonArray(val value: MutableList<JsonNode>) : JsonNode, List<JsonNode> by value

/**
 * Represents a JSON object.
 */
@JvmInline
value class JsonObject(val value: MutableMap<String, JsonNode>) : JsonNode, Map<String, JsonNode> by value

/**
 * Represents a JSON string.
 */
@JvmInline
value class JsonString(val value: String) : JsonNode, Comparable<String> by value, CharSequence by value

/**
 * Represents a JSON number.
 */
@JvmInline
value class JsonNumber(val value: Number) : JsonNode

/**
 * Represents a JSON boolean.
 */
@JvmInline
value class JsonBoolean(val value: Boolean) : JsonNode

/**
 * Represents a JSON null.
 */
object JsonNull : JsonNode