package simpleJson.exceptions

sealed class JsonException(override val message: String? = null, override val cause: Throwable? = null) : Exception()

class JsonParseException(message: String? = null, cause: Throwable? = null) : JsonException(message, cause)
class JsonEOFException(message: String? = null, cause: Throwable? = null) : JsonException(message, cause)
class JsonPropertyNotFoundException(message: String? = null, cause: Throwable? = null) : JsonException(message, cause)