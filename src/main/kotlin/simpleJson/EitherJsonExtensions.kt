package simpleJson

import arrow.core.Either
import arrow.core.flatMap
import simpleJson.exceptions.JsonException

//same as json extensions but for Either
operator fun Either<JsonException,JsonNode>.get(key: String): Either<JsonException, JsonNode> = flatMap { it[key] }

operator fun Either<JsonException,JsonNode>.get(index: Int): Either<JsonException, JsonNode> = flatMap { it[index] }

fun Either<JsonException,JsonNode>.getObject(key: String): Either<JsonException,JsonObject> = flatMap { it.getObject(key) }

fun Either<JsonException,JsonNode>.getArray(key: String): Either<JsonException, JsonArray> = flatMap { it.getArray(key) }

fun Either<JsonException,JsonNode>.getString(key: String): Either<JsonException,String> = flatMap { it.getString(key) }

fun Either<JsonException,JsonNode>.getNumber(key: String): Either<JsonException, Number> = flatMap { it.getNumber(key) }

fun Either<JsonException,JsonNode>.getInt(key: String): Either<JsonException, Int> = flatMap { it.getInt(key) }
fun Either<JsonException,JsonNode>.getDouble(key: String): Either<JsonException, Double> = flatMap { it.getDouble(key) }
fun Either<JsonException,JsonNode>.getFloat(key: String): Either<JsonException, Float> = flatMap { it.getFloat(key) }
fun Either<JsonException,JsonNode>.getLong(key: String): Either<JsonException, Long> = flatMap { it.getLong(key) }

fun Either<JsonException,JsonNode>.getBoolean(key: String): Either<JsonException,Boolean> = flatMap { it.getBoolean(key) }

fun Either<JsonException,JsonNode>.getNull(key: String): Either<JsonException, Nothing?> = flatMap { it.getNull(key) }

fun Either<JsonException,JsonNode>.toNumber(): Either<JsonException, Number> = flatMap { it.toNumber() }

fun Either<JsonException,JsonNode>.toInt(): Either<JsonException, Int> = flatMap { it.toInt() }
fun Either<JsonException,JsonNode>.toDouble(): Either<JsonException, Double> = flatMap { it.toDouble() }
fun Either<JsonException,JsonNode>.toFloat(): Either<JsonException, Float> = flatMap { it.toFloat() }
fun Either<JsonException,JsonNode>.toLong(): Either<JsonException, Long> = flatMap { it.toLong() }

fun Either<JsonException,JsonNode>.toBoolean(): Either<JsonException,Boolean> = flatMap { it.toBoolean() }

fun Either<JsonException,JsonNode>.toNull(): Either<JsonException, Nothing?> = flatMap { it.toNull() }

fun Either<JsonException,JsonNode>.to_String(): Either<JsonException,String> = flatMap { it.to_String() }

fun Either<JsonException,JsonNode>.toObject(): Either<JsonException,JsonObject> = flatMap { it.toObject() }

fun Either<JsonException,JsonNode>.toArray(): Either<JsonException,JsonArray> = flatMap { it.toArray() }



