package simpleJson

import arrow.core.Either
import arrow.core.continuations.either
import okio.Buffer
import okio.BufferedSource
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
class JsonReader(val reader: BufferedSource) {
    /**
     * JsonReader for the specified string
     */
    constructor(string: String) : this(Buffer().writeUtf8(string))

    private var current: Char? = null
    private var index = 0

    //After reading all json skip all whitespace and check for no more data after
    /**
     * Reads the input stream and returns a JsonNode
     * @return Either a JsonException or the JsonNode
     */
    fun read(): Either<JsonException, JsonNode> = run {
        readNextSkippingWhiteSpaces()
        readNode()
    }
        .mapLeft { JsonParseException("${it.message} at index ${index - 1}", it) }
        .mapLeft { if (current == null) JsonEOFException("Unexpected EOF at index ${index - 1}") else it }

    private fun readNode(): Either<JsonException, JsonNode> = either.eager {
        when (current) {
            JSON_LEFT_BRACKET -> readJsonArray().bind()
            JSON_LEFT_BRACE -> readJsonObject().bind()
            JSON_DOUBLE_QUOTE -> readString().bind()
            JSON_TRUE -> readBooleanTrue().bind()
            JSON_FALSE -> readBooleanFalse().bind()
            JSON_NULL -> readNull().bind()
            in STARTING_NUMBERS_CHARACTERS -> readNumber().bind()
            else -> shift(JsonParseException("Unexpected character $current"))
        }
    }

    private fun readJsonArray(): Either<JsonException, JsonArray> = either.eager {
        skipWhiteSpaces()

        if (current != JSON_LEFT_BRACKET) shift<JsonException>(JsonParseException("Expected '[' but found $current"))

        val list = mutableListOf<JsonNode>()

        readNextSkippingWhiteSpaces()
        if (current == JSON_RIGHT_BRACKET) {
            readNextSkippingWhiteSpaces()
            return@eager list
        }

        do readNode().bind()
            .also { list.add(it) }
            .also { skipWhiteSpaces() }
            .let { if (current == JSON_RIGHT_BRACKET) null else Unit }
            ?.also { if (current != JSON_COMMA) shift<JsonException>(JsonParseException("Expected ',' but found $current")) }
            ?.also { readNextSkippingWhiteSpaces() }
        while (current != JSON_RIGHT_BRACKET)

        readNextSkippingWhiteSpaces()
        list
    }.map(::JsonArray)

    private fun readJsonObject(): Either<JsonException, JsonObject> = either.eager {
        skipWhiteSpaces()

        if (current != JSON_LEFT_BRACE) shift<JsonException>(JsonParseException("Expected '{' but found $current"))

        val map = mutableMapOf<String, JsonNode>()

        readNextSkippingWhiteSpaces()
        if (current == JSON_RIGHT_BRACE) {
            readNextSkippingWhiteSpaces()
            return@eager map
        }

        do readString().bind()
            .also { if (current != JSON_COLON) shift<JsonException>(JsonParseException("Expected ':' but found $current")) }
            .also { readNextSkippingWhiteSpaces() }
            .let { map[it.value] = readNode().bind() }
            .let { if (current == JSON_RIGHT_BRACE) null else Unit }
            ?.also { if (current != JSON_COMMA) shift<JsonException>(JsonParseException("Expected ',' but found $current")) }
            ?.also { readNextSkippingWhiteSpaces() }
        while (current != JSON_RIGHT_BRACE)

        readNextSkippingWhiteSpaces()
        map
    }.map(::JsonObject)

    private fun readNumber(): Either<JsonException, JsonNumber> = either.eager {
        val resultBuilder = StringBuilder()

        while (current in NUMBERS_CHARACTERS) {
            resultBuilder.append(current)
            readNextSkippingWhiteSpaces()
        }

        val result = resultBuilder.toString()
        val parsed = result.toLongOrNull() ?: result.toDoubleOrNull()
        ?: shift(JsonParseException("Expected number but found $current"))

        JsonNumber(parsed)
    }

    private fun readBooleanTrue(): Either<JsonException, JsonBoolean> = either.eager {
        readLength("true".length - 1) { it == "true" }
            ?: shift<JsonException>(JsonParseException("Expected true but found $current"))

        JsonBoolean(true)
    }

    private fun readBooleanFalse(): Either<JsonException, JsonBoolean> = either.eager {
        readLength("false".length - 1) { it == "false" }
            ?: shift<JsonException>(JsonParseException("Expected false but found $current"))

        JsonBoolean(false)
    }

    private fun readNull(): Either<JsonException, JsonNull> = either.eager {
        readLength("null".length - 1) { it == "null" }
            ?: shift<JsonException>(JsonParseException("Expected null but found $current"))

        JsonNull
    }

    private fun readString(): Either<JsonException, JsonString> = either.eager {
        val builder = StringBuilder()

        if (current != JSON_DOUBLE_QUOTE) shift<JsonException>(JsonParseException("Expected '\"' but found $current"))

        fun readChar() = either.eager {
            if (current == '\\') {
                readEscapeSequence() ?: shift<JsonException>(JsonParseException("Invalid escape sequence"))
            } else current!!
        }

        do readNext()
            .let { if (current == JSON_DOUBLE_QUOTE) null else it }
            ?.let { readChar().bind() }
            ?.also { builder.append(it) }
        while (current != JSON_DOUBLE_QUOTE)

        readNextSkippingWhiteSpaces()
        JsonString(builder.toString())
    }

    private fun readEscapeSequence(): String? {
        readNext()
        return when (val escaped = current) {
            'u' -> readUnicodeSequence()
            else -> CONTROL_CHARACTERS[escaped].also { current = null }
        }
    }

    private fun readUnicodeSequence(): String? {
        val unicode = run {
            val builder = StringBuilder()
            repeat(4) {
                readNext()
                builder.append(current)
            }
            builder.toString()
        }
        return unicode.toIntOrNull(16)?.toChar()?.toString()
    }

    private fun readNext() {
        if (reader.exhausted()) {
            current = null
            return
        }
        current = reader.readByte().toInt().toChar()
        index++
    }

    private tailrec fun skipWhiteSpaces() {
        if (current != null && current !in WHITESPACE) return
        readNext()
        if (current == null) return
        skipWhiteSpaces()
    }

    private fun readNextSkippingWhiteSpaces() {
        readNext()
        skipWhiteSpaces()
    }

    private inline fun readLength(length: Int, predicate: (String) -> Boolean): String? = with(reader) {
        val result = StringBuilder().append(current)

        repeat(length) {
            readNext()
            result.append(current)
        }

        val resultString = result.toString()
        if (!predicate(resultString)) {
            return null
        }

        readNextSkippingWhiteSpaces()
        return resultString
    }


    companion object {
        fun read(string: String): Either<JsonException, JsonNode> = JsonReader(string).read()
        fun read(source: BufferedSource): Either<JsonException, JsonNode> =
            JsonReader(source).read()
    }
}

