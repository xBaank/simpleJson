package simpleJson

import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset

//Inverse map of CONTROL_CHARACTERS version in JsonReader
private val CONTROL_CHARACTERS_ESCAPED = mapOf(
    '\\' to "\\\\",
    '"' to "\\\"",
    '\b' to "\\b",
    '\n' to "\\n",
    '\r' to "\\r",
    '\u000c' to "\\f",
    '\t' to "\\t",
    '/' to "/"
)

interface IJsonWriter {
    fun write(node: JsonNode)
    fun writeArray(node: JsonArray)
    fun writeObject(node: JsonObject)
    fun writeString(node: JsonString)
    fun writeNumber(node: JsonNumber)
    fun writeBoolean(node: JsonBoolean)
    fun writeNull()
}

class JsonWriter(stream: OutputStream, charset: Charset = Charsets.UTF_8) : IJsonWriter {
    internal val writer = stream.bufferedWriter(charset)

    override fun write(node: JsonNode) = when (node) {
        is JsonArray -> writeArray(node)
        is JsonObject -> writeObject(node)
        is JsonString -> writeString(node)
        is JsonNumber -> writeNumber(node)
        is JsonBoolean -> writeBoolean(node)
        JsonNull -> writeNull()
    }.also { writer.flush() }

    override fun writeArray(node: JsonArray) {
        writer.write("[")
        node.value.forEachIndexed { index, jsonNode ->
            if (index != 0) writer.write(",")
            write(jsonNode)
        }
        writer.write("]")
    }

    override fun writeObject(node: JsonObject) {
        writer.write("{")
        node.value.entries.forEachIndexed { index, (key, value) ->
            if (index != 0) writer.write(",")
            writeString(JsonString(key))
            writer.write(":")
            write(value)
        }
        writer.write("}")
    }

    override fun writeString(node: JsonString) {
        writer.write("\"")
        writer.writeEscaped(node.value)
        writer.write("\"")
    }

    override fun writeNumber(node: JsonNumber) {
        writer.write(node.value.toString())
    }

    override fun writeBoolean(node: JsonBoolean) {
        writer.write(node.value.toString())
    }

    override fun writeNull() {
        writer.write("null")
    }

    companion object {
        fun write(node: JsonNode): String {
            val stream = ByteArrayOutputStream()
            JsonWriter(stream).write(node)
            return stream.toString()
        }

        fun write(node: JsonNode, stream: OutputStream, charset: Charset) =
            JsonWriter(stream, charset).write(node)
    }
}

class PrettyJsonWriter(private val jsonWriter: JsonWriter, val indent: String = "  ") : IJsonWriter by jsonWriter {
    private var indentLevel = 0
    private val writer = jsonWriter.writer

    override fun writeArray(node: JsonArray) {
        writer.write("[")
        if (node.value.isNotEmpty()) {
            writer.newLine()
            indentLevel++
            node.value.forEachIndexed { index, jsonNode ->
                if (index != 0) {
                    writer.write(",")
                    writer.newLine()
                }
                writeIndent()
                write(jsonNode)
            }
            writer.newLine()
            indentLevel--
            writeIndent()
        }
        writer.write("]")
    }

    override fun writeObject(node: JsonObject) {
        writer.write("{")
        if (node.value.isNotEmpty()) {
            writer.newLine()
            indentLevel++
            node.value.entries.forEachIndexed { index, (key, value) ->
                if (index != 0) {
                    writer.write(",")
                    writer.newLine()
                }
                writeIndent()
                writeString(JsonString(key))
                writer.write(": ")
                write(value)
            }
            writer.newLine()
            indentLevel--
            writeIndent()
        }
        writer.write("}")
    }

    private fun writeIndent() {
        repeat(indentLevel) {
            writer.write(indent)
        }
    }

    companion object {
        fun write(node: JsonNode): String {
            val stream = ByteArrayOutputStream()
            JsonWriter(stream).prettyPrint().write(node)
            return stream.toString()
        }

        fun write(node: JsonNode, stream: OutputStream, charset: Charset = Charsets.UTF_8) =
            JsonWriter(stream, charset).prettyPrint().write(node)
    }
}

fun BufferedWriter.writeEscaped(value: String) = value.forEach { char ->
    val escaped = CONTROL_CHARACTERS_ESCAPED.getOrDefault(char, null)
    if (escaped != null) write(escaped) else write(char.code)
}
