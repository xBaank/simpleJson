import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset

class JsonWriter(stream: OutputStream, charset: Charset = Charsets.UTF_8) {
    private val writer = stream.bufferedWriter(charset)

        fun write(node: JsonNode) = when (node) {
            is JsonArray -> writeArray(node)
            is JsonObject -> writeObject(node)
            is JsonString -> writeString(node)
            is JsonNumber -> writeNumber(node)
            is JsonBoolean -> writeBoolean(node)
            JsonNull -> writeNull()
        }.also { writer.flush() }

        private fun writeArray(node: JsonArray) {
            writer.write("[")
            node.value.forEachIndexed { index, jsonNode ->
                if (index != 0) writer.write(",")
                write(jsonNode)
            }
            writer.write("]")
        }

        private fun writeObject(node: JsonObject) {
            writer.write("{")
            node.value.entries.forEachIndexed { index, (key, value) ->
                if (index != 0) writer.write(",")
                writeString(JsonString(key))
                writer.write(":")
                write(value)
            }
            writer.write("}")
        }

        private fun writeString(node: JsonString) {
            writer.write("\"")
            writer.write(node.value)
            writer.write("\"")
        }

        private fun writeNumber(node: JsonNumber) {
            writer.write(node.value.toString())
        }

        private fun writeBoolean(node: JsonBoolean) {
            writer.write(node.value.toString())
        }

        private fun writeNull() {
            writer.write("null")
        }

    companion object {
        fun write(node: JsonNode) : String {
            val stream = ByteArrayOutputStream()
            JsonWriter(stream).write(node)
            return stream.toString()
        }

        fun write(node: JsonNode, stream: OutputStream, charset: Charset = Charsets.UTF_8) =
            JsonWriter(stream, charset).write(node)
    }
}

class PrettyJsonWriter(stream: OutputStream, charset: Charset = Charsets.UTF_8) {
    private val writer = stream.bufferedWriter(charset)
    private var indent = 0
    private val indentSize = "  "

    fun write(node: JsonNode) = when (node) {
        is JsonArray -> writeArray(node)
        is JsonObject -> writeObject(node)
        is JsonString -> writeString(node)
        is JsonNumber -> writeNumber(node)
        is JsonBoolean -> writeBoolean(node)
        JsonNull -> writeNull()
    }.also { writer.flush() }

    private fun writeArray(node: JsonArray) {
        writer.write("[")
        if(node.value.isNotEmpty()) {
            writer.newLine()
            indent++
            node.value.forEachIndexed { index, jsonNode ->
                if (index != 0) {
                    writer.write(",")
                    writer.newLine()
                }
                writeIndent()
                write(jsonNode)
            }
            writer.newLine()
            indent--
            writeIndent()
        }
        writer.write("]")
    }

    private fun writeObject(node: JsonObject) {
        writer.write("{")
        if(node.value.isNotEmpty()) {
            writer.newLine()
            indent++
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
            indent--
            writeIndent()
        }
        writer.write("}")
    }

    private fun writeString(node: JsonString) {
        writer.write("\"")
        writer.write(node.value)
        writer.write("\"")
    }

    private fun writeNumber(node: JsonNumber) {
        writer.write(node.value.toString())
    }

    private fun writeBoolean(node: JsonBoolean) {
        writer.write(node.value.toString())
    }

    private fun writeNull() {
        writer.write("null")
    }

    private fun writeIndent() {
        repeat(indent) {
            writer.write(indentSize)
        }
    }

    companion object {
        fun write(node: JsonNode) : String {
            val stream = ByteArrayOutputStream()
            PrettyJsonWriter(stream).write(node)
            return stream.toString()
        }

        fun write(node: JsonNode, stream: OutputStream, charset: Charset = Charsets.UTF_8) =
            PrettyJsonWriter(stream, charset).write(node)
    }
}