package simpleJson

import arrow.core.Either
import simpleJson.exceptions.JsonEOFException
import simpleJson.exceptions.JsonException
import simpleJson.exceptions.JsonParseException

private val WHITESPACE = arrayOf(' ', '\t', '\r', '\n')
private val CONTROL_CHARACTERS = mapOf(
    '\\' to "\\",
    '"' to "\"",
    '/' to "/",
    'b' to "\b",
    'u' to "", //No value, it gets constructed
    'n' to "\n",
    'f' to "\u000c",
    'r' to "\r",
    't' to "\t"
)
private val STARTING_NUMBERS_CHARACTERS = arrayOf('-', '+', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
private val NUMBERS_CHARACTERS = STARTING_NUMBERS_CHARACTERS + arrayOf('.', 'e', 'E')

/**
 * JsonReader for the specified input stream with the specified charset
 */
internal class JsonReader(private val data: String) {
    private var index = 0
    private inline val current: Char
        get() {
            if (data[index] == NULL_TERMINATOR) throw IndexOutOfBoundsException()
            return data[index]
        }
    private inline val exhausted get() = index >= data.length


    /**
     * Reads the input stream and returns a JsonNode
     * @return Either a JsonException or the JsonNode
     */
    fun read(): Either<JsonException, JsonNode> = Either.catch {
        skipWhiteSpaces()
        readNode()
    }.mapLeft {
        when (it) {
            is JsonException -> it
            is IndexOutOfBoundsException -> JsonEOFException("Unexpected EOF at index ${index - 1}")
            else -> JsonParseException(it.message ?: "Unknown error", it)
        }
    }

    private fun readNode() = when (current) {
        JSON_LEFT_BRACKET -> readJsonArray()
        JSON_LEFT_BRACE -> readJsonObject()
        JSON_DOUBLE_QUOTE -> readString()
        JSON_TRUE -> readBooleanTrue()
        JSON_FALSE -> readBooleanFalse()
        JSON_NULL -> readNull()
        in STARTING_NUMBERS_CHARACTERS -> readNumber()
        else -> throw JsonParseException("Unexpected character $current")
    }


    private fun readJsonArray(): JsonArray {
        skipWhiteSpaces()

        if (current != JSON_LEFT_BRACKET) throw JsonParseException("Expected '[' but found $current")

        val list = mutableListOf<JsonNode>()

        moveNext()
        skipWhiteSpaces()

        if (current == JSON_RIGHT_BRACKET) {
            moveNext()
            skipWhiteSpaces()
            return JsonArray(list)
        }

        do {
            readNode()
                .also { list.add(it) }
                .also { skipWhiteSpaces() }

            if (current == JSON_RIGHT_BRACKET) break
            if (current == JSON_COMMA) {
                moveNext()
                skipWhiteSpaces()
                continue
            }
            throw JsonParseException("Expected ',' or ']' but found $current")

        } while (current != JSON_RIGHT_BRACKET)

        moveNext()
        skipWhiteSpaces()
        return JsonArray(list)
    }

    private fun readJsonObject(): JsonObject {
        skipWhiteSpaces()

        if (current != JSON_LEFT_BRACE) throw JsonParseException("Expected '{' but found $current")

        val map = mutableMapOf<String, JsonNode>()

        moveNext()
        skipWhiteSpaces()

        if (current == JSON_RIGHT_BRACE) {
            moveNext()
            skipWhiteSpaces()
            return JsonObject(map)
        }

        do {
            readString()
                .also { skipWhiteSpaces() }
                .also { if (current != JSON_COLON) throw JsonParseException("Expected ':' but found $current") }
                .also { moveNext() }
                .also { skipWhiteSpaces() }
                .let { map[it.value] = readNode() }
                .also { skipWhiteSpaces() }

            if (current == JSON_RIGHT_BRACE) break
            if (current == JSON_COMMA) {
                moveNext()
                skipWhiteSpaces()
                continue
            }

            throw JsonParseException("Expected ',' or '}' but found $current")

        } while (current != JSON_RIGHT_BRACE)

        moveNext()
        skipWhiteSpaces()
        return JsonObject(map)
    }

    private fun readNumber(): JsonNumber {
        val resultBuilder = StringBuilder()

        while (current in NUMBERS_CHARACTERS) {
            resultBuilder.append(current)
            moveNext()
        }

        val result = resultBuilder.toString()
        val parsed = result.toLongOrNull() ?: result.toDoubleOrNull()
        ?: throw JsonParseException("Expected number but found $result")

        return JsonNumber(parsed)
    }

    private fun readBooleanTrue(): JsonBoolean {
        val exceptionFunc: (String) -> Nothing = { throw JsonParseException("Expected true but found $it") }
        readOrThrow("true".length - 1, exceptionFunc) { it == "true" }
        moveNext()

        return JsonBoolean(true)
    }

    private fun readBooleanFalse(): JsonBoolean {
        val exceptionFunc: (String) -> Nothing = { throw JsonParseException("Expected false but found $it") }
        readOrThrow("false".length - 1, exceptionFunc) { it == "false" }
        moveNext()

        return JsonBoolean(false)
    }

    private fun readNull(): JsonNull {
        val exceptionFunc: (String) -> Nothing = { throw JsonParseException("Expected null but found $it") }
        readOrThrow("null".length - 1, exceptionFunc) { it == "null" }
        moveNext()

        return JsonNull
    }

    private fun readString(): JsonString {
        val builder = StringBuilder()

        if (current != JSON_DOUBLE_QUOTE) throw JsonParseException("Expected '\"' but found $current")

        fun readChar() =
            if (current == '\\') {
                readEscapeSequence() ?: throw JsonParseException("Invalid escape sequence")
            } else current


        moveNext()
        do {
            if (current == JSON_DOUBLE_QUOTE) break
            builder.append(readChar())
            moveNext()
        } while (current != JSON_DOUBLE_QUOTE)

        moveNext()
        return JsonString(builder.toString())
    }

    private fun readEscapeSequence(): String? {
        moveNext()
        return when (val escaped = current) {
            'u' -> readUnicodeSequence()
            else -> CONTROL_CHARACTERS[escaped] //This is a hack, current can be " here
        }
    }

    private fun readUnicodeSequence(): String? {
        val unicode = run {
            val builder = StringBuilder()
            repeat(4) {
                moveNext()
                builder.append(current)
            }
            builder.toString()
        }
        return unicode.toIntOrNull(16)?.toChar()?.toString()
    }

    private fun moveNext() {
        index++
    }

    private tailrec fun skipWhiteSpaces() {
        if (!exhausted && current !in WHITESPACE) return
        moveNext()
        if (exhausted) return
        skipWhiteSpaces()
    }

    private inline fun readOrThrow(
        length: Int,
        exception: (String) -> Nothing,
        predicate: (String) -> Boolean,
    ): String {
        val result = StringBuilder().append(current)

        repeat(length) {
            moveNext()
            result.append(current)
        }

        val resultString = result.toString()
        if (!predicate(resultString)) {
            exception(resultString)
        }

        skipWhiteSpaces()
        return resultString
    }
}

