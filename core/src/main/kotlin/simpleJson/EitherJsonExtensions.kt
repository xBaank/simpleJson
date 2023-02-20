package simpleJson

import arrow.core.Either
import arrow.core.flatMap
import simpleJson.exceptions.JsonException

//same as json extensions but for Either
operator fun Either<JsonException, JsonNode>.set(key: String, value: JsonNode): Either<JsonException, Unit> =
    flatMap { it.set(key,value) }
operator fun Either<JsonException, JsonNode>.set(index: Int, value: JsonNode): Either<JsonException, Unit> =
    flatMap { it.set(index,value) }
operator fun Either<JsonException, JsonNode>.set(key: String, value: String): Either<JsonException, Unit> =
    flatMap { it.set(key,value) }
operator fun Either<JsonException, JsonNode>.set(key: String, value: Number): Either<JsonException, Unit> =
    flatMap { it.set(key,value) }
operator fun Either<JsonException, JsonNode>.set(key: String, value: Boolean): Either<JsonException, Unit> =
    flatMap { it.set(key,value) }
operator fun Either<JsonException, JsonNode>.set(key: String, value: Nothing?): Either<JsonException, Unit> =
    flatMap { it.set(key,value) }
operator fun Either<JsonException, JsonNode>.set(index: Int, value: String): Either<JsonException, Unit> =
    flatMap { it.set(index,value) }
operator fun Either<JsonException, JsonNode>.set(index: Int, value: Number): Either<JsonException, Unit> =
    flatMap { it.set(index,value) }
operator fun Either<JsonException, JsonNode>.set(index: Int, value: Boolean): Either<JsonException, Unit> =
    flatMap { it.set(index,value) }
operator fun Either<JsonException, JsonNode>.set(index: Int, value: Nothing?): Either<JsonException, Unit> =
    flatMap { it.set(index,value) }
operator fun Either<JsonException, JsonNode>.get(key: String): Either<JsonException, JsonNode> = flatMap { it[key] }

operator fun Either<JsonException, JsonNode>.get(index: Int): Either<JsonException, JsonNode> = flatMap { it[index] }

fun Either<JsonException, JsonNode>.getObject(key: String): Either<JsonException, JsonObject> =
    flatMap { it.getObject(key) }

fun Either<JsonException, JsonNode>.getArray(key: String): Either<JsonException, JsonArray> =
    flatMap { it.getArray(key) }

fun Either<JsonException, JsonNode>.getString(key: String): Either<JsonException, String> =
    flatMap { it.getString(key) }

fun Either<JsonException, JsonNode>.getNumber(key: String): Either<JsonException, Number> =
    flatMap { it.getNumber(key) }

fun Either<JsonException, JsonNode>.getInt(key: String): Either<JsonException, Int> = flatMap { it.getInt(key) }
fun Either<JsonException, JsonNode>.getDouble(key: String): Either<JsonException, Double> =
    flatMap { it.getDouble(key) }

fun Either<JsonException, JsonNode>.getFloat(key: String): Either<JsonException, Float> = flatMap { it.getFloat(key) }
fun Either<JsonException, JsonNode>.getLong(key: String): Either<JsonException, Long> = flatMap { it.getLong(key) }

fun Either<JsonException, JsonNode>.getBoolean(key: String): Either<JsonException, Boolean> =
    flatMap { it.getBoolean(key) }

fun Either<JsonException, JsonNode>.getNull(key: String): Either<JsonException, Nothing?> = flatMap { it.getNull(key) }

fun Either<JsonException, JsonNode>.asNumber(): Either<JsonException, Number> = flatMap { it.asNumber() }

fun Either<JsonException, JsonNode>.asInt(): Either<JsonException, Int> = flatMap { it.asInt() }
fun Either<JsonException, JsonNode>.asDouble(): Either<JsonException, Double> = flatMap { it.asDouble() }
fun Either<JsonException, JsonNode>.asFloat(): Either<JsonException, Float> = flatMap { it.asFloat() }
fun Either<JsonException, JsonNode>.asLong(): Either<JsonException, Long> = flatMap { it.asLong() }
fun Either<JsonException, JsonNode>.asShort(): Either<JsonException, Short> = flatMap { it.asShort() }
fun Either<JsonException, JsonNode>.asByte(): Either<JsonException, Byte> = flatMap { it.asByte() }

fun Either<JsonException, JsonNode>.asBoolean(): Either<JsonException, Boolean> = flatMap { it.asBoolean() }

fun Either<JsonException, JsonNode>.asNull(): Either<JsonException, Nothing?> = flatMap { it.asNull() }

fun Either<JsonException, JsonNode>.asString(): Either<JsonException, String> = flatMap { it.asString() }

fun Either<JsonException, JsonNode>.asObject(): Either<JsonException, JsonObject> = flatMap { it.asObject() }

fun Either<JsonException, JsonNode>.asArray(): Either<JsonException, JsonArray> = flatMap { it.asArray() }



