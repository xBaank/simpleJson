package simpleJson.reflection

import simpleJson.exceptions.JsonException

sealed class JsonReflectionException(override val message: String?, override val cause: JsonException? = null) :
    Exception()

class JsonDeserializationException(override val message: String?, override val cause: JsonException? = null) :
    JsonReflectionException(message, cause)

class JsonSerializationException(override val message: String?, override val cause: JsonException? = null) :
    JsonReflectionException(message, cause)