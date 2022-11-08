import exceptions.JsonException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI

internal class JsonReaderTest {

    @Test
    fun readUnicode() {
        val data = "{\"a\": \"\u00e9\"}"
        val json = JsonReader.read(data)
        assert(json.tryGetString("a") == "é")
    }

    @Test
    fun readNumber() {
        val data = "{\"a\": 1}"
        val json = JsonReader.read(data)
        val number = json.tryGetInt("a")
        assert(number == 1)
    }

    @Test
    fun readNegativeNumber() {
        val data = "{\"a\": -1}"
        val json = JsonReader.read(data)
        val number = json.tryGetInt("a")
        assert(number == -1)
    }

    @Test
    fun readPlusNumber() {
        val data = "{\"a\": +1}"
        val json = JsonReader.read(data)
        val number = json.tryGetInt("a")
        assert(number == 1)
    }

    @Test
    fun readDecimalNumber() {
        val data = "{\"a\": 1.1}"
        val json = JsonReader.read(data)
        val number = json.tryGetDouble("a")
        assert(number == 1.1)
    }

    @Test
    fun readNegativeDecimalNumber() {
        val data = "{\"a\": -1.1}"
        val json = JsonReader.read(data)
        val number = json.tryGetDouble("a")
        assert(number == -1.1)
    }

    @Test
    fun readPlusDecimalNumber() {
        val data = "{\"a\": +1.1}"
        val json = JsonReader.read(data)
        val number = json.tryGetDouble("a")
        assert(number == 1.1)
    }

    @Test
    fun readBoolean() {
        val data = "{\"a\": true}"
        val json = JsonReader.read(data)
        assert(json.tryGetBoolean("a") == true)
    }

    @Test
    fun readArray() {
        val data = "{\"a\": [1, 2, 3]}"
        val json = JsonReader.read(data)
        val array = json.tryGetArray("a")
        assert(array?.size == 3)
    }

    @Test
    fun readObject() {
        val data = "{\"a\": {\"b\": 1}}"
        val json = JsonReader.read(data)
        val obj = json.tryGetObject("a")
        assert(obj?.tryGetNumber("b")?.toInt() == 1)
    }

    @Test
    fun readNull() {
        val data = "{\"a\": null}"
        val json = JsonReader.read(data)
        assert(json.tryGetNull("a") == JsonNull)
    }

    @Test
    fun readEmptyObject() {
        val data = "{}"
        val json = JsonReader.read(data)
        assert(json is JsonObject)
    }

    @Test
    fun `should only read object`() {
        val data = "[]"
        assertThrows<JsonException> { JsonReader.read(data) }
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
        assert(json.tryGetString("a") ==  "\n \t \"hola\" \t \n")
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
                "f": [1, 2, 3],
                "g": {
                    "h": 1
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
    fun `should json with nested objects`() {
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
        assert(json.tryGetObject("g")?.tryGetInt("h") == 1)
        assert(json.tryGetObject("g")?.tryGetObject("i")?.tryGetInt("j") == 1)
    }

    @Test
    fun `should read from stream`() {
        val data = URI.create("https://jsonplaceholder.typicode.com/photos").toURL().openStream()
        val json = JsonReader.read(data) as JsonArray
        assert(json.value.size == 5000)
        assert(json.value.map {  it.tryGetProperty("albumId")}.all { it != null })
        assert(json.value.map {  it.tryGetProperty("id")}.all { it != null })
        assert(json.value.map {  it.tryGetProperty("title")}.all { it != null })
        assert(json.value.map {  it.tryGetProperty("url")}.all { it != null })
        assert(json.value.map {  it.tryGetProperty("thumbnailUrl")}.all { it != null })
    }
}