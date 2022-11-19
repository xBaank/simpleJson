sealed interface JsonNode
data class JsonArray(val value: List<JsonNode>) : JsonNode, List<JsonNode> by value
data class JsonObject(val value: Map<String, JsonNode>) : JsonNode, Map<String, JsonNode> by value
data class JsonString(val value: String) : JsonNode, CharSequence by value
data class JsonNumber(val value: Number) : JsonNode
data class JsonBoolean(val value: Boolean) : JsonNode
object JsonNull : JsonNode