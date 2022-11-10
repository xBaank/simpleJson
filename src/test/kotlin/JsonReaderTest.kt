import exceptions.JsonException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

internal class JsonReaderTest {

    @Test
    fun `should read unicode`() {
        val data = "{\"a\": \"\u00e9\"} "
        val json = JsonReader.read(data)
        assert(json.tryGetString("a") == "é")
    }

    @Test
    fun `should read number`() {
        val data = "{\"a\": 1}"
        val json = JsonReader.read(data)
        val number = json.tryGetInt("a")
        assert(number == 1)
    }

    @Test
    fun `should read number with -`() {
        val data = "{\"a\": -1}"
        val json = JsonReader.read(data)
        val number = json.tryGetInt("a")
        assert(number == -1)
    }

    @Test
    fun `should read number with +`() {
        val data = "{\"a\": +1}"
        val json = JsonReader.read(data)
        val number = json.tryGetInt("a")
        assert(number == 1)
    }

    @Test
    fun `should read decimal number`() {
        val data = "{\"a\": 1.1}"
        val json = JsonReader.read(data)
        val number = json.tryGetDouble("a")
        assert(number == 1.1)
    }

    @Test
    fun `should read decimal number with -`() {
        val data = "{\"a\": -1.1}"
        val json = JsonReader.read(data)
        val number = json.tryGetDouble("a")
        assert(number == -1.1)
    }

    @Test
    fun `should read decimal number with +`() {
        val data = "{\"a\": +1.1}"
        val json = JsonReader.read(data)
        val number = json.tryGetDouble("a")
        assert(number == 1.1)
    }

    @Test
    fun `should read boolean`() {
        val data = "{\"a\": true}"
        val json = JsonReader.read(data)
        assert(json.tryGetBoolean("a") == true)
    }

    @Test
    fun `should read array`() {
        val data = " [ 1 , 2 , 3 ] "
        val json = JsonReader.read(data)
        val array = json as JsonArray
        assert(array.value.size == 3)
    }

    @Test
    fun `should read array with all types`() {
        val data = " [ 1 , 2 , 3, \"a\", true, false, null, {\"a\": 1}, [1, 2, 3] ] "
        val json = JsonReader.read(data)
        val array = json.tryGetArray()
        assert(array?.size == 9)
        assert(array?.getOrNull(0)?.tryGetInt() == 1)
        assert(array?.getOrNull(1)?.tryGetInt() == 2)
        assert(array?.getOrNull(2)?.tryGetInt() == 3)
        assert(array?.getOrNull(3)?.tryGetString() == "a")
        assert(array?.getOrNull(4)?.tryGetBoolean() == true)
        assert(array?.getOrNull(5)?.tryGetBoolean() == false)
        assert(array?.getOrNull(6)?.tryGetNull() == JsonNull)
        assert(array?.getOrNull(7)?.tryGetObject()?.tryGetInt("a") == 1)
        assert(array?.getOrNull(8)?.tryGetArray()?.size == 3)
        assert(array?.getOrNull(8)?.tryGetArray()?.getOrNull(0)?.tryGetInt() == 1)
        assert(array?.getOrNull(8)?.tryGetArray()?.getOrNull(1)?.tryGetInt() == 2)
        assert(array?.getOrNull(8)?.tryGetArray()?.getOrNull(2)?.tryGetInt() == 3)

    }

    @Test
    fun `should read array in object`() {
        val data = "{ \"a\" : [ 1 , 2 , 3 ] }"
        val json = JsonReader.read(data)
        val array = json.tryGetArray("a")
        assert(array?.size == 3)
    }

    @Test
    fun `should read object`() {
        val data = "{\"a\": {\"b\": 1}}"
        val json = JsonReader.read(data)
        val obj = json.tryGetObject("a")
        assert(obj?.tryGetNumber("b")?.toInt() == 1)
    }

    @Test
    fun `should read null`() {
        val data = "{\"a\": null}"
        val json = JsonReader.read(data)
        assert(json.tryGetNull("a") == JsonNull)
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
        assert(json.tryGetString("a") == "\n \t \"hola\" \t \n")
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
        assert(json.tryGetInt("a") == 1)
        assert(json.tryGetString("b") == "2")
        assert(json.tryGetBoolean("c") == true)
        assert(json.tryGetBoolean("d") == false)
        assert(json.tryGetNull("e") == JsonNull)
        assert(json.tryGetArray(" f ")?.size == 3)
        assert(json.tryGetObject("g")?.tryGetInt("h") == 1)
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
        assert(json.tryGetInt("a") == 1)
        assert(json.tryGetString("b") == "2")
        assert(json.tryGetBoolean("c") == true)
        assert(json.tryGetBoolean("d") == false)
        assert(json.tryGetNull("e") == JsonNull)
        assert(json.tryGetArray("f")?.size == 3)
        assert(json.tryGetProperty("g")?.tryGetInt("h") == 1)
        assert(json.tryGetProperty("g")?.tryGetProperty("i")?.tryGetInt("j") == 1)
    }

    @Test
    fun `should json read from stream`() {
        val data = File("src/test/resources/photos.json").inputStream()
        val json = JsonReader.read(data) as JsonArray
        assert(json.value.size == 5000)
        assert(json.value.map { it.tryGetProperty("albumId") }.all { it != null })
        assert(json.value.map { it.tryGetProperty("id") }.all { it != null })
        assert(json.value.map { it.tryGetProperty("title") }.all { it != null })
        assert(json.value.map { it.tryGetProperty("url") }.all { it != null })
        assert(json.value.map { it.tryGetProperty("thumbnailUrl") }.all { it != null })
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

        val json = JsonReader.tryRead(data)
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

        val json = JsonReader.tryRead(data)
        assert(json == null)
    }
}
