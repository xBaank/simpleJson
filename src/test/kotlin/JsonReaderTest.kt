import exceptions.JsonException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

internal class JsonReaderTest {

    @Test
    fun `should read unicode`() {
        val data = "{\"a\": \"\\u00e9\"} "
        val json = JsonReader.read(data)
        assert(json.getStringOrNull("a") == "é")
    }

    @Test
    fun `should read backslash`() {
        val data = "{\"a\": \"\\\"\"} "
        val json = JsonReader.read(data)
        assert(json.getStringOrNull("a") == "\"")
    }

    @Test
    fun `should read slash`() {
        val data = "{\"a\": \"\\/\"} "
        val json = JsonReader.read(data)
        assert(json.getStringOrNull("a") == "/")
    }

    @Test
    fun `should read backspace`() {
        val data = "{\"a\": \"\b\"} "
        val json = JsonReader.read(data)
        assert(json.getStringOrNull("a") == "\b")
    }

    @Test
    fun `should read newline`() {
        val data = "{\"a\": \"\n\"} "
        val json = JsonReader.read(data)
        assert(json.getStringOrNull("a") == "\n")
    }

    @Test
    fun `should read formfeed`() {
        val data = "{\"a\": \"\u000c\"} "
        val json = JsonReader.read(data)
        assert(json.getStringOrNull("a") == "\u000c")
    }

    @Test
    fun `should read carriage return`() {
        val data = "{\"a\": \"\r\"} "
        val json = JsonReader.read(data)
        assert(json.getStringOrNull("a") == "\r")
    }

    @Test
    fun `should read tab`() {
        val data = "{\"a\": \"\t\"} "
        val json = JsonReader.read(data)
        assert(json.getStringOrNull("a") == "\t")
    }

    @Test
    fun `should read number`() {
        val data = "{\"a\": 1}"
        val json = JsonReader.read(data)
        val number = json.getIntOrNull("a")
        assert(number == 1)
    }

    @Test
    fun `should read number with -`() {
        val data = "{\"a\": -1}"
        val json = JsonReader.read(data)
        val number = json.getIntOrNull("a")
        assert(number == -1)
    }

    @Test
    fun `should read number with +`() {
        val data = "{\"a\": +1}"
        val json = JsonReader.read(data)
        val number = json.getIntOrNull("a")
        assert(number == 1)
    }

    @Test
    fun `should read decimal number`() {
        val data = "{\"a\": 1.1}"
        val json = JsonReader.read(data)
        val number = json.getDoubleOrNull("a")
        assert(number == 1.1)
    }

    @Test
    fun `should read decimal number with -`() {
        val data = "{\"a\": -1.1}"
        val json = JsonReader.read(data)
        val number = json.getDoubleOrNull("a")
        assert(number == -1.1)
    }

    @Test
    fun `should read decimal number with +`() {
        val data = "{\"a\": +1.1}"
        val json = JsonReader.read(data)
        val number = json.getDoubleOrNull("a")
        assert(number == 1.1)
    }

    @Test
    fun `should read boolean`() {
        val data = "{\"a\": true}"
        val json = JsonReader.read(data)
        assert(json.getBooleanOrNull("a") == true)
    }

    @Test
    fun `should read array`() {
        val data = " [ 1 , 2 , 3,4.4,\"a\",{}, [[[]]], true, false, null ] "
        val json = JsonReader.read(data)
        assert(json.toArrayOrNull()?.size == 10)
        assert(json.getPropertyOrNull(0)?.toIntOrNull() == 1)
        assert(json.getPropertyOrNull(1)?.toDoubleOrNull() == 2.0)
        assert(json.getPropertyOrNull(2)?.toLongOrNull() == 3L)
        assert(json.getPropertyOrNull(3)?.toFloatOrNull() == 4.4f)
        assert(json.getPropertyOrNull(4)?.toStringOrNull() == "a")
        assert(json.getPropertyOrNull(5)?.toObjectOrNull()?.value?.isEmpty() == true)
        assert(json.getPropertyOrNull(6)?.toArrayOrNull()?.size == 1)
        assert(json.getPropertyOrNull(7)?.toBooleanOrNull() == true)
        assert(json.getPropertyOrNull(8)?.toBooleanOrNull() == false)
        assert(json.getPropertyOrNull(9) is JsonNull)
    }

    @Test
    fun `should read array with all types`() {
        val data = " [ 1 , 2 , 3, \"a\", true, false, null, {\"a\": 1}, [1, 2, 3] ] "
        val json = JsonReader.read(data)
        val array = json.toArrayOrNull()
        assert(array?.size == 9)
        assert(array?.getOrNull(0)?.toIntOrNull() == 1)
        assert(array?.getOrNull(1)?.toIntOrNull() == 2)
        assert(array?.getOrNull(2)?.toIntOrNull() == 3)
        assert(array?.getOrNull(3)?.toStringOrNull() == "a")
        assert(array?.getOrNull(4)?.toBooleanOrNull() == true)
        assert(array?.getOrNull(5)?.toBooleanOrNull() == false)
        assert(array?.getOrNull(6)?.toJsonNullOrNull() == JsonNull)
        assert(array?.getOrNull(7)?.toObjectOrNull()?.getIntOrNull("a") == 1)
        assert(array?.getOrNull(8)?.toArrayOrNull()?.size == 3)
        assert(array?.getOrNull(8)?.toArrayOrNull()?.getOrNull(0)?.toIntOrNull() == 1)
        assert(array?.getOrNull(8)?.toArrayOrNull()?.getOrNull(1)?.toIntOrNull() == 2)
        assert(array?.getOrNull(8)?.toArrayOrNull()?.getOrNull(2)?.toIntOrNull() == 3)

    }

    @Test
    fun `should read array in object`() {
        val data = "{ \"a\" : [ 1 , 2 , 3 ] }"
        val json = JsonReader.read(data)
        val array = json.getArrayOrNull("a")
        assert(array?.size == 3)
        assert(array?.getOrNull(0)?.toIntOrNull() == 1)
        assert(array?.getOrNull(1)?.toIntOrNull() == 2)
        assert(array?.getOrNull(2)?.toIntOrNull() == 3)
    }

    @Test
    fun `should read object`() {
        val data = "{\"a\": {\"b\": 1}}"
        val json = JsonReader.read(data)
        val obj = json.getObjectOrNull("a")
        assert(obj?.getNumberOrNull("b")?.toInt() == 1)
    }

    @Test
    fun `should read null`() {
        val data = "{\"a\": null}"
        val json = JsonReader.read(data)
        assert(json.getJsonNullOrNull("a") == JsonNull)
    }

    @Test
    fun `should read empty object`() {
        val data = "{}"
        val json = JsonReader.read(data)
        assert(json is JsonObject)
    }

    @Test
    fun `should empty array`() {
        val data = "[]"
        val json = JsonReader.read(data)
        assert(json is JsonArray)
    }

    @Test
    fun `should not read empty string`() {
        val data = ""
        assertThrows<JsonException> { JsonReader.read(data) }
    }

    @Test
    fun `should not read empty string with spaces`() {
        val data = "   "
        assertThrows<JsonException> { JsonReader.read(data) }
    }

    @Test
    fun `should read empty string with spaces and newlines`() {
        val data = """
            {
                "a": "\n \t \"hola\" \t \n"
            }
        """.trimIndent()
        val json = JsonReader.read(data)
        assert(json.getStringOrNull("a") == "\n \t \"hola\" \t \n")
    }

    @Test
    fun `should read json`() {
        val data = """
            {
                "a": 1,
                "b": "2",
                "c": true,
                "d": false,
                "e": null,
                 " f "  :  [   1  , 2     , 3    ]    , 
                 
                "g": {
                    "h": 1
                }
            }
        """
        val json = JsonReader.read(data)
        assert(json.getIntOrNull("a") == 1)
        assert(json.getStringOrNull("b") == "2")
        assert(json.getBooleanOrNull("c") == true)
        assert(json.getBooleanOrNull("d") == false)
        assert(json.getJsonNullOrNull("e") == JsonNull)
        assert(json.getArrayOrNull(" f ")?.size == 3)
        assert(json.getObjectOrNull("g")?.getIntOrNull("h") == 1)
    }

    @Test
    fun `should not read json`() {
        val data = """
            {
                "a": 1, asd
                "b": "2",
                "c": true,
                "d": false,
                "e": null,
                "f": [1, 2, 3],
                "g": {
                    "h": 1
                }
        """.trimIndent()
        assertThrows<JsonException> { JsonReader.read(data) }
    }

    @Test
    fun `should not read json with invalid number`() {
        val data = """
            {
                "a": 1,
                "b": "2",
                "c": true,
                "d": false,
                "e": null,
                "f": [1, 2, 3],
                "g": {
                    "h": 1
                },
                "i": 1.1.1
            }
        """.trimIndent()
        assertThrows<JsonException> { JsonReader.read(data) }
    }

    @Test
    fun `should read json with nested objects`() {
        val data = """
            {
                "a": 1,
                "b": "2",
                "c": true,
                "d": false,
                "e": null,
                "f": [1, 2, 3],
                "g": {
                    "h": 1,
                    "i": {
                        "j": 1
                    }
                }
            }
        """.trimIndent()
        val json = JsonReader.read(data)
        assert(json.getIntOrNull("a") == 1)
        assert(json.getStringOrNull("b") == "2")
        assert(json.getBooleanOrNull("c") == true)
        assert(json.getBooleanOrNull("d") == false)
        assert(json.getJsonNullOrNull("e") == JsonNull)
        assert(json.getArrayOrNull("f")?.size == 3)
        assert(json.getPropertyOrNull("g")?.getIntOrNull("h") == 1)
        assert(json.getPropertyOrNull("g")?.getPropertyOrNull("i")?.getIntOrNull("j") == 1)
    }

    @Test
    fun `should json read from stream`() {
        val data = File("src/test/resources/photos.json").inputStream()
        val json = JsonReader.read(data) as JsonArray
        assert(json.value.size == 5000)
        assert(json.value.map { it.getPropertyOrNull("albumId") }.all { it != null })
        assert(json.value.map { it.getPropertyOrNull("id") }.all { it != null })
        assert(json.value.map { it.getPropertyOrNull("title") }.all { it != null })
        assert(json.value.map { it.getPropertyOrNull("url") }.all { it != null })
        assert(json.value.map { it.getPropertyOrNull("thumbnailUrl") }.all { it != null })
    }

    @Test
    fun `should try parse json`() {
        val data = """
            {
                "a": 1,
                "b": "2",
                "c": true,
                "d": false,
                "e": null,
                "f": [1, 2, 3],
                "g": {
                    "h": 1,
                    "i": {
                        "j": 1
                    }
                }
            }
        """.trimIndent()

        val json = JsonReader.readOrNull(data)
        assert(json != null)
    }

    @Test
    fun `try parse should return null`() {
        val data = """
            {
                "a": 1, asd
                "b": "2",
                "c": true,
                "d": false,
                "e": null,
                "f":  [ 1, 2, 3],
                "g": {
                    "h": 1,
                    "i": {
                        "j": 1
                    }
                }
            }
        """.trimIndent()

        val json = JsonReader.readOrNull(data)
        assert(json == null)
    }

    @Test
    fun `should deserialize`() {
        val data = """
            {
                "a": 1,
                "b": "2",
                "c": true,
                "d": false,
                "e": null,
                "f": [1, 2, 3],
                "g": {
                    "h": 1,
                    "i": {
                        "j": 1
                    }
                }
            }
        """.trimIndent()

        val json = data.deserialize()
        assert(json.getIntOrNull("a") == 1)
        assert(json.getStringOrNull("b") == "2")
        assert(json.getBooleanOrNull("c") == true)
        assert(json.getBooleanOrNull("d") == false)
        assert(json.getJsonNullOrNull("e") == JsonNull)
        assert(json.getArrayOrNull("f")?.size == 3)
        assert(json.getPropertyOrNull("g")?.getIntOrNull("h") == 1)
        assert(json.getPropertyOrNull("g")?.getPropertyOrNull("i")?.getIntOrNull("j") == 1)
    }

    @Test
    fun `should not deserialize`() {
        val data = """
            {
                "a": 1,
                "b": "2", asd
                "c": true,
                "d": false,
                "e": null,
                "f": [1, 2, 3],
                "g": {
                    "h": 1,
                    "i": {
                        "j": 1
                    }
                }
            }
        """.trimIndent()

        val json = data.deserializeOrNull()
        assert(json == null)
    }
}
