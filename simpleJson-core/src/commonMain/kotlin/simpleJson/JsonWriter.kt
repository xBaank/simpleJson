package simpleJson

import arrow.core.getOrNone
import okio.BufferedSink

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

/**
 * Interface for writing JsonNodes
 */
internal interface IJsonWriter {
    val writer: BufferedSink
    fun writeArray(node: JsonArray)
    fun writeObject(node: JsonObject)
    fun writeString(node: JsonString)
    fun writeNumber(node: JsonNumber)
    fun writeBoolean(node: JsonBoolean)
    fun writeNull()
}

/**
 * Writes the specified node to the writer
 * @param node The node to write
 */
internal fun IJsonWriter.write(node: JsonNode) = when (node) {
    is JsonArray -> writeArray(node)
    is JsonObject -> writeObject(node)
    is JsonString -> writeString(node)
    is JsonNumber -> writeNumber(node)
    is JsonBoolean -> writeBoolean(node)
    JsonNull -> writeNull()
}.also { writer.flush() }

/**
 * Implementation of IJsonWriter for writing to an output stream
 */
internal class JsonWriter(override val writer: BufferedSink) : IJsonWriter {

    override fun writeArray(node: JsonArray) {
        writer.writeUtf8("[")
        node.value.forEachIndexed { index, jsonNode ->
            if (index != 0) writer.writeUtf8(",")
            write(jsonNode)
        }
        writer.writeUtf8("]")
    }

    override fun writeObject(node: JsonObject) {
        writer.writeUtf8("{")
        node.value.entries.forEachIndexed { index, (key, value) ->
            if (index != 0) writer.writeUtf8(",")
            writeString(JsonString(key))
            writer.writeUtf8(":")
            write(value)
        }
        writer.writeUtf8("}")
    }

    override fun writeString(node: JsonString) {
        writer.writeUtf8("\"")
        writer.writeEscaped(node.value)
        writer.writeUtf8("\"")
    }

    override fun writeNumber(node: JsonNumber) {
        writer.writeUtf8(node.value.toString())
    }

    override fun writeBoolean(node: JsonBoolean) {
        writer.writeUtf8(node.value.toString())
    }

    override fun writeNull() {
        writer.writeUtf8("null")
    }
}

/**
 * Implementation of IJsonWriter for writing to an output stream with pretty printing
 */
internal class PrettyJsonWriter(private val jsonWriter: JsonWriter, val indent: String = "  ") :
    IJsonWriter by jsonWriter {
    private var indentLevel = 0
    override val writer = jsonWriter.writer

    override fun writeArray(node: JsonArray) {
        writer.writeUtf8("[")
        if (node.value.isNotEmpty()) {
            writer.newLine()
            indentLevel++
            node.value.forEachIndexed { index, jsonNode ->
                if (index != 0) {
                    writer.writeUtf8(",")
                    writer.newLine()
                }
                writeIndent()
                write(jsonNode)
            }
            writer.newLine()
            indentLevel--
            writeIndent()
        }
        writer.writeUtf8("]")
    }

    override fun writeObject(node: JsonObject) {
        writer.writeUtf8("{")
        if (node.value.isNotEmpty()) {
            writer.newLine()
            indentLevel++
            node.value.entries.forEachIndexed { index, (key, value) ->
                if (index != 0) {
                    writer.writeUtf8(",")
                    writer.newLine()
                }
                writeIndent()
                writeString(JsonString(key))
                writer.writeUtf8(": ")
                write(value)
            }
            writer.newLine()
            indentLevel--
            writeIndent()
        }
        writer.writeUtf8("}")
    }

    private fun writeIndent() {
        repeat(indentLevel) {
            writer.writeUtf8(indent)
        }
    }
}

private fun BufferedSink.writeEscaped(value: String) = value.forEach { char ->
    val escaped = CONTROL_CHARACTERS_ESCAPED.getOrNone(char).getOrNull()
    if (escaped != null) writeUtf8(escaped) else writeUtf8CodePoint(char.code)
}

private fun BufferedSink.newLine() = writeUtf8("\n")
