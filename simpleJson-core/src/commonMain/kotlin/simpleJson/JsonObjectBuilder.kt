package simpleJson

/**
 * Marker annotation for the [JsonObjectBuilder] and [JsonArrayBuilder] DSL.
 */
@DslMarker
internal annotation class JsonDsl

@JsonDsl
class JsonObjectBuilder {

    private val map = mutableMapOf<String, JsonNode>()

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    infix fun String.to(value: String?) =
        if (value == null) map.put(this, JsonNull) else map.put(this, JsonString(value))

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    infix fun String.to(value: Number?) =
        if (value == null) map.put(this, JsonNull) else map.put(this, JsonNumber(value))

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    infix fun String.to(value: Boolean?) =
        if (value == null) map.put(this, JsonNull) else map.put(this, JsonBoolean(value))

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    infix fun String.to(value: JsonNode) = map.put(this, value)

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    @Suppress("UNUSED_PARAMETER")
    infix fun String.to(value: Nothing?) = map.put(
        this,
        JsonNull
    ) //Nullable nothing can only be null or nothing but nothing is nothing, so it is just null

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    operator fun String.plusAssign(value: String?) {
        this to value
    }

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    operator fun String.plusAssign(value: Number?) {
        this to value
    }

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    operator fun String.plusAssign(value: Boolean?) {
        this to value
    }

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    operator fun String.plusAssign(value: JsonNode) {
        this to value
    }

    /**
     * Adds the [value] to the object with [String] as its key.
     */
    operator fun String.plusAssign(value: Nothing?) {
        this to value
    }

    @Deprecated("Deprecated in favor of String.plusAssign or to method", ReplaceWith("name += jObject(block)"))
    inline fun jObject(name: String, block: JsonObjectBuilder.() -> Unit = {}) =
        name to jObject(block)

    @Deprecated("Deprecated in favor of String.plusAssign or to method", ReplaceWith("name += jArray(block)"))
    inline fun jArray(name: String, block: JsonArrayBuilder.() -> Unit = {}) =
        name to jArray(block)

    /**
     * builds the [JsonObject] using the [JsonObjectBuilder] DSL.
     */
    fun build() = JsonObject(map)

}

@JsonDsl
class JsonArrayBuilder {

    private val list = mutableListOf<JsonNode>()

    /**
     * Adds the [String] to the array.
     */
    operator fun String?.unaryPlus() {
        add(this)
    }

    /**
     * Adds the [Number] to the array.
     */
    operator fun Number?.unaryPlus() {
        add(this)
    }

    /**
     * Adds the [Boolean] to the array.
     */
    operator fun Boolean?.unaryPlus() {
        add(this)
    }

    /**
     * Adds the [Nothing] (null) to the array.
     */
    operator fun Nothing?.unaryPlus() {
        add(this)
    }

    /**
     * Adds the [JsonNode] to the array.
     */
    operator fun JsonNode.unaryPlus() {
        add(this)
    }

    /**
     * Adds the [value] to the array.
     */
    fun add(value: String?) = if (value != null) list.add(JsonString(value)) else list.add(JsonNull)

    /**
     * Adds the [value] to the array.
     */
    fun add(value: Number?) = if (value != null) list.add(JsonNumber(value)) else list.add(JsonNull)

    /**
     * Adds the [value] to the array.
     */
    fun add(value: Boolean?) = if (value != null) list.add(JsonBoolean(value)) else list.add(JsonNull)

    /**
     * Adds the [value] to the array.
     */
    @Suppress("UNUSED_PARAMETER")
    fun add(value: Nothing?) = list.add(JsonNull)

    /**
     * Adds the [value] to the array.
     */
    fun add(value: JsonNode) = list.add(value)

    //As union types are not supported in Kotlin neither custom implicit conversions do, We need to ask for a node directly,
    //but we can still use the DSL converting supported types to nodes with asJson() extension functions
    /**
     * Adds the [values] to the array.
     */
    fun addAll(vararg values: JsonNode) = list.addAll(values)

    /**
     * add the [JsonObject] to the array using the [JsonObjectBuilder] DSL.
     */
    inline fun addObject(block: JsonObjectBuilder.() -> Unit = {}) = add(jObject(block))

    /**
     * add the [JsonArray] to the array using the [JsonArrayBuilder] DSL.
     */
    inline fun addArray(block: JsonArrayBuilder.() -> Unit = {}) = add(jArray(block))

    /**
     * builds the [JsonArray] using the [JsonArrayBuilder] DSL.
     */
    fun build() = JsonArray(list)

}

/**
 * Creates a [JsonObject] using the [JsonObjectBuilder] DSL.
 */
inline fun jObject(init: JsonObjectBuilder.() -> Unit) = JsonObjectBuilder().apply(init).build()

/**
 * Creates a [JsonArray] using the [JsonArrayBuilder] DSL.
 */
inline fun jArray(init: JsonArrayBuilder.() -> Unit) = JsonArrayBuilder().apply(init).build()

/**
 * Creates a [JsonArray] using the [values] as its properties.
 */
fun jArray(vararg values: JsonNode) = jArray { addAll(*values) }