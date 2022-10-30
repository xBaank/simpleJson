import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

private const val WHITESPACE: Char = ' '
private val CONTROL_CHARACTERS = arrayOf("\"", "\\", "/", "\b", "\u000c", "\n", "\r", "\t")

object JsonReader {
    private var current: Char? = null
    inline fun <reified T> read(data: String, charset: Charset = Charsets.UTF_8): T {
        read(data.byteInputStream(), charset)
        return null as T
    }

    fun read(inputStream: ByteArrayInputStream, charset: Charset = Charsets.UTF_8) =
        inputStream.bufferedReader(charset).use { read(it) }

    private fun read(reader: BufferedReader) {
        do {
            reader.skipWhiteSpaces()

            when (current) {
                JSON_LEFT_BRACE -> readObject(reader)
                JSON_LEFT_BRACKET -> readArray(reader)
                JSON_DOUBLE_QUOTE -> readString(reader)
            }

            reader.skipWhiteSpaces()

        } while (current != null)
    }

    private fun BufferedReader.readOrEof(): Char? = read().takeIf { it != -1 }?.toChar()

    private tailrec fun BufferedReader.skipWhiteSpaces() {
        current = readOrEof()

        if (current == WHITESPACE)
            skipWhiteSpaces()

        return
    }

    private fun readString(reader: BufferedReader): JsonString {
        return JsonString("")
    }

    private fun readNumber(data: String): JsonNumber {
        return JsonNumber(data.toDouble())
    }

    private fun readBoolean(data: String): JsonBoolean {
        return JsonBoolean(data.toBoolean())
    }

    private fun readNull(data: String): JsonNull {
        return JsonNull
    }

    private fun readArray(reader: BufferedReader): JsonArray {
        return JsonArray(listOf())
    }

    private fun readObject(reader: BufferedReader): JsonObject {
        do {
            reader.skipWhiteSpaces()

            when (current) {
                JSON_LEFT_BRACE -> readObject(reader)
                JSON_LEFT_BRACKET -> readArray(reader)
                JSON_DOUBLE_QUOTE -> readString(reader)
            }

            reader.skipWhiteSpaces()

        } while (current != null)
    }

}