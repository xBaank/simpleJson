package simpleJson

import arrow.core.Either
import arrow.core.rightIfNotNull
import simpleJson.exceptions.JsonException
import java.io.BufferedReader
import java.io.InputStream
import java.nio.charset.Charset

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
private val NUMBERS_CHARACTERS = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-', '+')

class JsonReader(inputStream: InputStream, charset: Charset = Charsets.UTF_8) {
    constructor(string: String) : this(string.byteInputStream())

    private val reader = BufferedReader(inputStream.reader(charset))
    private var current: Char? = null

    //After reading all json skip all whitespace and check for no more data after
    fun read(): Either<JsonException, JsonNode> = readOrNull()
        .takeIf { skipWhiteSpaces(); current == null }
        .rightIfNotNull { JsonException("Unexpected  character $current, on line ${reader.readLine()}") }

    private fun readOrNull(): JsonNode? = readObjectOrNull() ?: readArrayOrNull()

    private fun readNext() {
        current = readOrEof()
    }

    private fun readOrEof(): Char? = reader.read().takeIf { it != -1 }?.toChar()

    private inline fun readLength(length: Int, predicate: (String) -> Boolean): String? = with(reader) {
        mark(length)
        val result = StringBuilder().append(current)

        repeat(length) {
            result.append(readOrEof())
        }
        val resultString = result.toString()
        if (!predicate(resultString)) {
            reset()
            return null
        }

        readNext()
        return resultString
    }

    private tailrec fun skipWhiteSpaces() {
        if (current != null && current !in WHITESPACE) return
        readNext()
        if (current == null) return
        skipWhiteSpaces()
    }

    private fun readStringOrNull(): JsonString? {
        if (current != JSON_DOUBLE_QUOTE)
            return null

        val result = StringBuilder()
        readNext() // skip first quote

        while (current != JSON_DOUBLE_QUOTE) {

            if (current == '\\') {
                current = readOrEof().takeIf { it in CONTROL_CHARACTERS } ?: return null

                if (current == 'u') {

                    val charArray = CharArray(4)
                    val read = reader.read(charArray)

                    if (read != 4) return null

                    val unicode = String(charArray).toInt(16)
                    result.append(unicode.toChar())

                } else result.append(CONTROL_CHARACTERS[current])

            } else result.append(current)


            readNext()
        }

        readNext() // skip last quote

        return JsonString(result.toString())
    }

    //TODO add support for exponential numbers
    private fun readNumberOrNull(): JsonNumber? {
        val resultBuilder = StringBuilder()

        while (current in NUMBERS_CHARACTERS) {
            resultBuilder.append(current)
            readNext()
        }

        val result = resultBuilder.toString()

        return JsonNumber(result.toLongOrNull() ?: result.toDoubleOrNull() ?: return null)
    }

    private fun readBooleanOrNull(): JsonBoolean? {
        val result = readLength("true".length - 1) { it == "true" }
            ?: readLength("false".length - 1) { it == "false" } ?: return null
        return JsonBoolean(result == "true")
    }

    private fun readNullOrNull(): JsonNull? {
        readLength("null".length - 1) { it == "null" } ?: return null
        return JsonNull
    }

    private fun readArrayOrNull(): JsonArray? {
        withoutWhitespaces { if (current != JSON_LEFT_BRACKET) return null }


        val array = mutableListOf<JsonNode>()
        do {
            val item =
                readValue()
                    ?: break

            array.add(item)
        } while (current == JSON_COMMA)

        withoutWhitespaces { if (current != JSON_RIGHT_BRACKET) return null }
        readNext()
        return JsonArray(array)
    }

    private fun readKey(): String? {
        readNext()
        val key = withoutWhitespaces { readStringOrNull() ?: return null }
        if (current != JSON_COLON) return null
        return key.value
    }

    private fun readValue(): JsonNode? = withoutWhitespaces {
        readNext()
        readObjectOrNull() ?: readArrayOrNull() ?: readStringOrNull() ?: readNumberOrNull()
        ?: readBooleanOrNull() ?: readNullOrNull()
    }

    private fun readObjectOrNull(): JsonObject? {

        val objectMap = mutableMapOf<String, JsonNode>()

        withoutWhitespaces {
            if (current != JSON_LEFT_BRACE) return null

            do {

                val key = readKey() ?: break
                val value = readValue() ?: return null
                objectMap[key] = value

            } while (current == JSON_COMMA)
        }

        withoutWhitespaces { if (current != JSON_RIGHT_BRACE) return null }
        readNext()
        return JsonObject(objectMap)
    }

    private inline fun <T> withoutWhitespaces(block: () -> T): T {
        skipWhiteSpaces()
        val result: T = block()
        skipWhiteSpaces()
        return result
    }

    companion object {
        fun read(string: String): Either<JsonException, JsonNode> = JsonReader(string).read()
        fun read(inputStream: InputStream, charset: Charset = Charsets.UTF_8): Either<JsonException, JsonNode> =
            JsonReader(inputStream, charset).read()
    }
}

