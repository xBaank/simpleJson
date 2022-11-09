import exceptions.JsonException
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
    fun read(): JsonNode = tryRead()
        .takeIf { skipWhiteSpaces(); current == null } ?: throw JsonException("Unexpected  character $current")

    fun tryRead(): JsonNode? = readObjectOrNull() ?: readArrayOrNull()

    private fun readNext() {
        current = readOrEof()
    }

    private fun readOrEof(): Char? = reader.read().takeIf { it != -1 }?.toChar()

    private fun readLength(length: Int, predicate: (String) -> Boolean): String? = with(reader) {
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

                    result.append(charArray.toString())
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
            val item = withoutWhitespaces {
                readNext()
                readObjectOrNull() ?: readArrayOrNull() ?: readStringOrNull() ?: readNumberOrNull()
                ?: readBooleanOrNull() ?: readNullOrNull()
            } ?: break

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
        readNext()
        return key.value
    }

    private fun readObjectOrNull(): JsonObject? {

        val objectMap = mutableMapOf<String, JsonNode>()

        withoutWhitespaces {
            if (current != JSON_LEFT_BRACE) return null

            do {

                val key = readKey() ?: break

                withoutWhitespaces {
                    val value =
                        readObjectOrNull() ?: readArrayOrNull() ?: readStringOrNull() ?: readNumberOrNull()
                        ?: readBooleanOrNull() ?: readNullOrNull() ?: return null

                    objectMap.put(key, value)
                }

            } while (current == JSON_COMMA)
        }

        withoutWhitespaces { if (current != JSON_RIGHT_BRACE) return null }

        readNext()

        return JsonObject(objectMap)
    }

    private inline fun <T> withoutWhitespaces(block: () -> T): T {
        val result: T?
        skipWhiteSpaces()
        result = block()
        skipWhiteSpaces()
        return result
    }

    companion object {
        fun read(string: String): JsonNode = JsonReader(string).read()
        fun read(inputStream: InputStream, charset: Charset = Charsets.UTF_8): JsonNode = JsonReader(inputStream).read()
        fun tryRead(inputStream: InputStream, charset: Charset = Charsets.UTF_8): JsonNode? =
            JsonReader(inputStream).tryRead()

        fun tryRead(string: String): JsonNode? = JsonReader(string).tryRead()
    }
}

