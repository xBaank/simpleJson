package simpleJson

@DslMarker
internal annotation class JsonDsl

@JsonDsl
class JsonObjectBuilder {

    private val map = mutableMapOf<String, JsonNode>()

    infix fun String.to(value: String) = map.put(this, JsonString(value))
    infix fun String.to(value: Number) = map.put(this, JsonNumber(value))
    infix fun String.to(value: Boolean) = map.put(this, JsonBoolean(value))
    infix fun String.to(value: JsonNode) = map.put(this, value)

    @Suppress("UNUSED_PARAMETER")
    infix fun String.to(value: Nothing?) = map.put(
        this,
        JsonNull
    ) //Nullable nothing can only be null or nothing but nothing is nothing, so it is just null

    operator fun String.plusAssign(value: String) {
        this to value
    }

    operator fun String.plusAssign(value: Number) {
        this to value
    }

    operator fun String.plusAssign(value: Boolean) {
        this to value
    }

    operator fun String.plusAssign(value: JsonNode) {
        this to value
    }

    operator fun String.plusAssign(value: Nothing?) {
        this to value
    }

    @Deprecated("Deprecated in favor of String.plusAssign or to method", ReplaceWith("name += jObject(block)"))
    fun jObject(name: String, block: JsonObjectBuilder.() -> Unit = {}) =
        name to jObject(block)

    @Deprecated("Deprecated in favor of String.plusAssign or to method", ReplaceWith("name += jArray(block)"))
    fun jArray(name: String, block: JsonArrayBuilder.() -> Unit = {}) =
        name to jArray(block)


    fun build() = JsonObject(map)

}

@JsonDsl
class JsonArrayBuilder {

    private val list = mutableListOf<JsonNode>()

    operator fun String.unaryPlus() {
        add(this)
    }

    operator fun Number.unaryPlus() {
        add(this)
    }

    operator fun Boolean.unaryPlus() {
        add(this)
    }

    operator fun Nothing?.unaryPlus() {
        add(this)
    }

    operator fun JsonNode.unaryPlus() {
        add(this)
    }

    fun add(value: String) = list.add(JsonString(value))
    fun add(value: Number) = list.add(JsonNumber(value))
    fun add(value: Boolean) = list.add(JsonBoolean(value))

    @Suppress("UNUSED_PARAMETER")
    fun add(value: Nothing?) = list.add(JsonNull)
    fun add(value: JsonNode) = list.add(value)

    //As union types are not supported in Kotlin neither custom implicit conversions do, We need to ask for a node directly,
    //but we can still use the DSL converting supported types to nodes with toJson() extension functions
    fun addAll(vararg values: JsonNode) = list.addAll(values)
    inline fun addObject(block: JsonObjectBuilder.() -> Unit = {}) = add(jObject(block))
    inline fun addArray(block: JsonArrayBuilder.() -> Unit = {}) = add(jArray(block))

    fun build() = JsonArray(list)

}

inline fun jObject(init: JsonObjectBuilder.() -> Unit) = JsonObjectBuilder().apply(init).build()

inline fun jArray(init: JsonArrayBuilder.() -> Unit) = JsonArrayBuilder().apply(init).build()

fun jArray(vararg values: JsonNode) = jArray { addAll(*values) }