package simpleJson

import arrow.core.getOrNone
import okio.Buffer
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

interface IJsonWriter {
    val writer: BufferedSink
    fun write(node: JsonNode)
}

/**
 * Implementation of IJsonWriter for writing to an output stream
 */
class JsonWriter(override val writer: BufferedSink) : IJsonWriter {
    @PublishedApi
    internal var deepness = 0

    private val deepFunction = DeepRecursiveFunction { nodeToWrite: JsonNode ->
        writeNode(
            nodeToWrite,
            { writeArray(it) { callRecursive(it) } },
            { writeObject(it) { callRecursive(it) } })
    }

    private val normalFunction = { nodeToWrite: JsonNode ->
        writeNode(
            nodeToWrite,
            { writeArray(it) { write(it) } },
            { writeObject(it) { write(it) } })
    }

    override fun write(node: JsonNode) {
        if (deepness > 200) deepFunction(node)
        else normalFunction(node)
    }

    @PublishedApi
    internal inline fun writeNode(
        node: JsonNode,
        writeArrayF: (JsonArray) -> Unit,
        writeObjectF: (JsonObject) -> Unit
    ) {
        when (node) {
            is JsonArray -> withDeepness { writeArrayF(node) }
            is JsonObject -> withDeepness { writeObjectF(node) }
            is JsonString -> writeString(node)
            is JsonNumber -> writeNumber(node)
            is JsonBoolean -> writeBoolean(node)
            JsonNull -> writeNull()
        }.also { writer.flush() }
    }


    private inline fun writeArray(node: JsonArray, writeFunction: (JsonNode) -> Unit) {
        writer.writeUtf8("[")
        node.value.forEachIndexed { index, jsonNode ->
            if (index != 0) writer.writeUtf8(",")
            writeFunction(jsonNode)
        }
        writer.writeUtf8("]")
    }

    private inline fun writeObject(node: JsonObject, writeFunction: (JsonNode) -> Unit) {
        writer.writeUtf8("{")
        node.value.entries.forEachIndexed { index, (key, value) ->
            if (index != 0) writer.writeUtf8(",")
            writeString(JsonString(key))
            writer.writeUtf8(":")
            writeFunction(value)
        }
        writer.writeUtf8("}")
    }

    @PublishedApi
    internal fun writeString(node: JsonString) {
        writer.writeUtf8("\"")
        writer.writeEscaped(node.value)
        writer.writeUtf8("\"")
    }

    @PublishedApi
    internal fun writeNumber(node: JsonNumber) {
        writer.writeUtf8(node.value.toString())
    }

    @PublishedApi
    internal fun writeBoolean(node: JsonBoolean) {
        writer.writeUtf8(node.value.toString())
    }

    @PublishedApi
    internal fun writeNull() {
        writer.writeUtf8("null")
    }

    @PublishedApi
    internal inline fun withDeepness(f: () -> Unit) {
        ++deepness
        f()
        deepness -= 1
    }

    companion object {
        fun write(node: JsonNode): String {
            val stream = Buffer()
            JsonWriter(stream).write(node)
            return stream.readUtf8()
        }

        fun write(node: JsonNode, sink: BufferedSink) =
            JsonWriter(sink).write(node)
    }
}

/**
 * Implementation of IJsonWriter for writing to an output stream with pretty printing
 */
class PrettyJsonWriter(private val jsonWriter: JsonWriter, val indent: String = "  ") : IJsonWriter by jsonWriter {
    private var indentLevel = 0

    @PublishedApi
    internal val deepFunction = DeepRecursiveFunction { nodeToWrite: JsonNode ->
        jsonWriter.writeNode(
            nodeToWrite,
            { writeArray(it) { callRecursive(it) } },
            { writeObject(it) { callRecursive(it) } })
    }

    @PublishedApi
    internal val normalFunction = { nodeToWrite: JsonNode ->
        jsonWriter.writeNode(
            nodeToWrite,
            { writeArray(it) { write(it) } },
            { writeObject(it) { write(it) } })
    }


    override fun write(node: JsonNode) {
        if (jsonWriter.deepness > 200) deepFunction(node)
        else normalFunction(node)
    }

    private inline fun writeArray(node: JsonArray, writeFunction: (JsonNode) -> Unit) {
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
                writeFunction(jsonNode)
            }
            writer.newLine()
            indentLevel--
            writeIndent()
        }
        writer.writeUtf8("]")
    }

    private inline fun writeObject(node: JsonObject, writeFunction: (JsonNode) -> Unit) {
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
                jsonWriter.writeString(JsonString(key))
                writer.writeUtf8(": ")
                writeFunction(value)
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

    companion object {
        fun write(node: JsonNode): String {
            val stream = Buffer()
            JsonWriter(stream).prettyPrint().write(node)
            return stream.readUtf8()
        }

        fun write(node: JsonNode, sink: BufferedSink) =
            JsonWriter(sink).prettyPrint().write(node)
    }
}

private fun BufferedSink.writeEscaped(value: String) = value.forEach { char ->
    val escaped = CONTROL_CHARACTERS_ESCAPED.getOrNone(char).orNull()
    if (escaped != null) writeUtf8(escaped) else writeUtf8CodePoint(char.code)
}

private fun BufferedSink.newLine() = writeUtf8("\n")
