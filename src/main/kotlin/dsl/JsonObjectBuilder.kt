package dsl

import JsonArray
import JsonBoolean
import JsonNode
import JsonNull
import JsonNumber
import JsonObject
import JsonString

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
    infix fun String.to(value: Nothing?) = map.put(this, JsonNull)

    fun build() = JsonObject(map)

}

@JsonDsl
class JsonArrayBuilder {

    private val list = mutableListOf<JsonNode>()

    fun add(value: String) = list.add(JsonString(value))
    fun add(value: Number) = list.add(JsonNumber(value))
    fun add(value: Boolean) = list.add(JsonBoolean(value))
    fun add(value: Nothing?) = list.add(JsonNull)
    fun add(value: JsonObject) = list.add(value)
    fun add(value: JsonArray) = list.add(value)


    //with plus operator
    operator fun String.unaryPlus() = add(this)
    operator fun Int.unaryPlus() = add(this)
    operator fun Double.unaryPlus() = add(this)


    operator fun Boolean.unaryPlus() = add(this)
    operator fun Nothing?.unaryPlus() = add(this)
    operator fun JsonObject.unaryPlus() = add(this)
    operator fun JsonArray.unaryPlus() = add(this)

    fun build() = JsonArray(list)

}

fun jObject(init: JsonObjectBuilder.() -> Unit) =
    JsonObjectBuilder().apply(init).build()

fun jArray(init: JsonArrayBuilder.() -> Unit) =
    JsonArrayBuilder().apply(init).build()