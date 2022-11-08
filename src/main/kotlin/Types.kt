sealed class JsonNode
data class JsonArray(val value: List<JsonNode>) : JsonNode()
data class JsonObject(val value: Map<String, JsonNode>) : JsonNode()
data class JsonString(val value: String) : JsonNode()
data class JsonNumber(val value: Number) : JsonNode()
data class JsonBoolean(val value: Boolean) : JsonNode()
object JsonNull : JsonNode()


