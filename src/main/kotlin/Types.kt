sealed class Json
data class JsonArray(val value: List<Json>) : Json()
data class JsonObject(val value: Values) : Json()
data class JsonString(val value: String) : Json()
data class JsonNumber(val value: Number) : Json()
data class JsonBoolean(val value: Boolean) : Json()
object JsonNull : Json()


typealias Values = List<Pair<String, Json>>
