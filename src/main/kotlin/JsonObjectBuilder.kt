@DslMarker
annotation class JsonDsl

@JsonDsl
class JsonObjectBuilder {

    private val map = mutableMapOf<String, JsonNode>()

    infix fun String.to(value: String) = map.put(this, JsonString(value))
    infix fun String.to(value: Number) = map.put(this, JsonNumber(value))
    infix fun String.to(value: Boolean) = map.put(this, JsonBoolean(value))
    infix fun String.to(value: JsonArray) = map.put(this, value)
    infix fun String.to(value: JsonObject) = map.put(this, value)
    infix fun String.to(value: Nothing?) = map.put(this, JsonNull) //Nullable nothing can only be null or nothing but nothing is nothing, so it is just null

    fun jObject(name : String ,block: JsonObjectBuilder.() -> Unit) = map.put(name, JsonObjectBuilder().apply(block).build())
    fun jArray(name : String ,block: JsonArrayBuilder.() -> Unit) = map.put(name, JsonArrayBuilder().apply(block).build())


    fun build() = JsonObject(map)

}

@JsonDsl
class JsonArrayBuilder {

    private val list = mutableListOf<JsonNode>()

    infix fun add(value: String) = list.add(JsonString(value))
    infix fun add(value: Number) = list.add(JsonNumber(value))
    infix fun add(value: Boolean) = list.add(JsonBoolean(value))
    infix fun add(value: Nothing?) = list.add(JsonNull)
    infix fun add(value: JsonObject) = list.add(value)
    infix fun add(value: JsonArray) = list.add(value)

    fun addObj(block: JsonObjectBuilder.() -> Unit) = list.add(JsonObjectBuilder().apply(block).build())
    fun addArray(block: JsonArrayBuilder.() -> Unit) = list.add(JsonArrayBuilder().apply(block).build())

    fun build() = JsonArray(list)

}

fun jObject(init: JsonObjectBuilder.() -> Unit) =
    JsonObjectBuilder().apply(init).build()

fun jArray(init: JsonArrayBuilder.() -> Unit) =
    JsonArrayBuilder().apply(init).build()