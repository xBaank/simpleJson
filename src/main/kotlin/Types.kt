sealed interface JsonNode

@JvmInline
value class JsonArray(val value: List<JsonNode>) : JsonNode, List<JsonNode> by value

@JvmInline
value class JsonObject(val value: Map<String, JsonNode>) : JsonNode, Map<String, JsonNode> by value

@JvmInline
value class JsonString(val value: String) : JsonNode, Comparable<String> by value, CharSequence by value

@JvmInline
value class JsonNumber(val value: Number) : JsonNode

@JvmInline
value class JsonBoolean(val value: Boolean) : JsonNode
object JsonNull : JsonNode