@DslMarker
internal annotation class JsonDsl

@JsonDsl
class JsonObjectBuilder {

    private val map = mutableMapOf<String, JsonNode>()

    infix fun String.to(value: String) = map.put(this, JsonString(value))
    infix fun String.to(value: Number) = map.put(this, JsonNumber(value))
    infix fun String.to(value: Boolean) = map.put(this, JsonBoolean(value))
    infix fun String.to(value: JsonArray) = map.put(this, value)
    infix fun String.to(value: JsonObject) = map.put(this, value)
    @Suppress("UNUSED_PARAMETER")
    infix fun String.to(value: Nothing?) = map.put(this,JsonNull) //Nullable nothing can only be null or nothing but nothing is nothing, so it is just null

    fun jObject(name: String, block: JsonObjectBuilder.() -> Unit) =
        map.put(name, jObject(block))

    fun jArray(name: String, block: JsonArrayBuilder.() -> Unit) =
        map.put(name, jArray(block))


    fun build() = JsonObject(map)

}

@JsonDsl
class JsonArrayBuilder {

    private val list = mutableListOf<JsonNode>()

    fun add(value: String) = list.add(JsonString(value))
    fun add(value: Number) = list.add(JsonNumber(value))
    fun add(value: Boolean) = list.add(JsonBoolean(value))
    @Suppress("UNUSED_PARAMETER")
    fun add(value: Nothing?) = list.add(JsonNull)
    fun add(value: JsonNode) = list.add(value)

    //As union types are not supported in Kotlin neither custom implicit conversions do, We need to ask for a node directly,
    //but we can still use the DSL converting supported types to nodes with toJson() extension functions
    fun addAll(vararg values : JsonNode) = list.addAll(values)
    fun addObject(block: JsonObjectBuilder.() -> Unit) = add(JsonObjectBuilder().apply(block).build())
    fun addArray(block: JsonArrayBuilder.() -> Unit) = add(JsonArrayBuilder().apply(block).build())

    fun build() = JsonArray(list)

}

fun jObject(init: JsonObjectBuilder.() -> Unit) =
    JsonObjectBuilder().apply(init).build()

fun jArray(init: JsonArrayBuilder.() -> Unit) =
    JsonArrayBuilder().apply(init).build()