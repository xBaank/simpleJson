import java.io.BufferedReader
import java.io.InputStream
import java.nio.charset.Charset

private val WHITESPACE: Array<String> = arrayOf(" ", "\t", "\r", "\n")
private val CONTROL_CHARACTERS = arrayOf("\"", "\\", "/", "\b", "\u000c", "\n", "\r", "\t")

object JsonReader {
    private var current: Char? = null

    fun read(inputStream: InputStream, charset: Charset = Charsets.UTF_8) : JsonNode =
        inputStream.bufferedReader(charset).use { readObjectOrNull(it) ?: throw Exception("Unexpected  character $current") }

    fun read(data: String, charset: Charset = Charsets.UTF_8): JsonNode = read(data.byteInputStream(), charset)


    private fun BufferedReader.readOrEof(): Char? = read().takeIf { it != -1 }?.toChar()

    private fun BufferedReader.readLength(length: Int, predicate: (String) -> Boolean): String? {
        mark(length)
        val result = StringBuilder().append(current)

        repeat(length) {
            result.append(readOrEof() ?: throw Exception("Unexpected end of file"))
        }
        val resultString = result.toString()
        if (!predicate(resultString)) {
            reset()
            return null
        }

        current = readOrEof()
        return resultString
    }

    private tailrec fun BufferedReader.skipWhiteSpaces() {
        if (current != null && current.toString() !in WHITESPACE) return

        current = readOrEof()
        skipWhiteSpaces()
        return
    }

    private fun readStringOrNull(reader: BufferedReader): JsonString? {

        if (current != JSON_DOUBLE_QUOTE)
            return null

        val result = StringBuilder()
        current = reader.readOrEof() // skip first quote

        while (current != JSON_DOUBLE_QUOTE) {
            if (current == '\\') {
                current = reader.readOrEof() // skip backslash
                if (current.toString() !in CONTROL_CHARACTERS)
                    throw Exception("Invalid control character")

                result.append(current)
            } else {
                result.append(current)
            }

            current = reader.readOrEof()
        }

        current = reader.readOrEof() // skip last quote

        return JsonString(result.toString())
    }

    private fun readNumberOrNull(reader: BufferedReader): JsonNumber? {
        val resultBuilder = StringBuilder()

        while (current.toString() in "0123456789") {
            resultBuilder.append(current)
            current = reader.readOrEof()
        }

        val result = resultBuilder.toString()

        return JsonNumber(result.toDoubleOrNull() ?: result.toLongOrNull() ?: return null)
    }

    private fun readBooleanOrNull(reader: BufferedReader): JsonBoolean? {
        reader.skipWhiteSpaces()
        val result = reader.readLength("true".length - 1) { it == "true" } ?: reader.readLength("false".length - 1) { it == "false" } ?: return null
        return JsonBoolean(result == "true")
    }

    private fun readNullOrNull(reader: BufferedReader): JsonNull? {
        reader.skipWhiteSpaces()
        reader.readLength("null".length - 1) { it == "null" } ?: return null
        return JsonNull
    }

    private fun readArrayOrNull(reader: BufferedReader): JsonArray? {
        reader.skipWhiteSpaces()
        if (current != JSON_LEFT_BRACKET) return null


        val array = mutableListOf<JsonNode>()
        do {
            current = reader.readOrEof()
            val item =
                readObjectOrNull(reader) ?:
                readArrayOrNull(reader) ?:
                readStringOrNull(reader) ?:
                readNumberOrNull(reader) ?:
                readBooleanOrNull(reader) ?:
                readNullOrNull(reader)

            if(item != null) array.add(item)
        } while (current == JSON_COMMA)

        reader.skipWhiteSpaces()
        // bracket
        if (current != JSON_RIGHT_BRACKET) return null
        reader.skipWhiteSpaces()
        current = reader.readOrEof()
        return JsonArray(array)
    }

    private fun readKey (reader: BufferedReader): String? {
        current = reader.readOrEof()
        reader.skipWhiteSpaces()
        val key = readStringOrNull(reader) ?: return null
        reader.skipWhiteSpaces()
        if (current != JSON_COLON) return null
        current = reader.readOrEof()
        return key.value
    }

    private fun readObjectOrNull(reader: BufferedReader): JsonObject? {
        reader.skipWhiteSpaces()

        if(current != JSON_LEFT_BRACE) return null

        val objectMap = mutableListOf<Pair<String, JsonNode>>()
        do {
            val key = readKey(reader) ?: break

            val value = readObjectOrNull(reader) ?:
                readArrayOrNull(reader) ?:
                readStringOrNull(reader) ?:
                readNumberOrNull(reader) ?:
                readBooleanOrNull(reader) ?:
                readNullOrNull(reader)
                ?: throw Exception("Unexpected character: $current")

            objectMap.add(key to value)
            reader.skipWhiteSpaces()

        } while (current == JSON_COMMA)

        reader.skipWhiteSpaces()
        if(current != JSON_RIGHT_BRACE) return null
        reader.skipWhiteSpaces()
        current = reader.readOrEof()

        return JsonObject(objectMap)
    }

}