import arrow.core.Either
import arrow.core.flatMap
import org.junit.jupiter.api.Test
import simpleJson.*
import simpleJson.exceptions.JsonException
import java.io.File

internal class JsonReaderTest {

    @Test
    fun `should read unicode`() {
        val data = "{\"a\": \"\\u00e9\"} "
        val json = JsonReader.read(data)
        assert(json.flatMap { it.getString("a") }.getOrThrow() == "é")
    }

    @Test
    fun `should read backslash`() {
        val data = "{\"a\": \"\\\"\"} "
        val json = JsonReader.read(data)
        assert(json.getString("a").getOrThrow() == "\"")
    }

    @Test
    fun `should read slash`() {
        val data = "{\"a\": \"\\/\"} "
        val json = JsonReader.read(data)
        assert(json.getString("a").getOrThrow() == "/")
    }

    @Test
    fun `should read backspace`() {
        val data = "{\"a\": \"\b\"} "
        val json = JsonReader.read(data)
        assert(json.getString("a").getOrThrow() == "\b")
    }

    @Test
    fun `should read newline`() {
        val data = "{\"a\": \"\n\"} "
        val json = JsonReader.read(data)
        assert(json.getString("a").getOrThrow() == "\n")
    }

    @Test
    fun `should read formfeed`() {
        val data = "{\"a\": \"\u000c\"} "
        val json = JsonReader.read(data)
        assert(json.getString("a").getOrThrow() == "\u000c")
    }

    @Test
    fun `should read carriage return`() {
        val data = "{\"a\": \"\r\"} "
        val json = JsonReader.read(data)
        assert(json.getString("a").getOrThrow() == "\r")
    }

    @Test
    fun `should read tab`() {
        val data = "{\"a\": \"\t\"} "
        val json = JsonReader.read(data)
        assert(json.getString("a").getOrThrow() == "\t")
    }

    @Test
    fun `should read number`() {
        val data = "{\"a\": 1}"
        val json = JsonReader.read(data)
        val number = json.getInt("a")
        assert(number.getOrThrow() == 1)
    }

    @Test
    fun `should read number with -`() {
        val data = "{\"a\": -1}"
        val json = JsonReader.read(data)
        val number = json.getInt("a")
        assert(number.getOrThrow() == (-1))
    }

    @Test
    fun `should read number with +`() {
        val data = "{\"a\": +1}"
        val json = JsonReader.read(data)
        val number = json.getInt("a")
        assert(number.getOrThrow() == 1)
    }

    @Test
    fun `should read decimal number`() {
        val data = "{\"a\": 1.1}"
        val json = JsonReader.read(data)
        val number = json.getDouble("a")
        assert(number.getOrThrow() == 1.1)
    }

    @Test
    fun `should read decimal number with -`() {
        val data = "{\"a\": -1.1}"
        val json = JsonReader.read(data)
        val number = json.getDouble("a")
        assert(number.getOrThrow() == (-1.1))
    }

    @Test
    fun `should read decimal number with +`() {
        val data = "{\"a\": +1.1}"
        val json = JsonReader.read(data)
        val number = json.getDouble("a")
        assert(number.getOrThrow() == 1.1)
    }

    @Test
    fun `should read boolean`() {
        val data = "{\"a\": true}"
        val json = JsonReader.read(data)
        assert(json.getBoolean("a").getOrThrow())
    }

    @Test
    fun `should read array`() {
        val data = " [ 1 , 2 , 3,4.4,\"a\",{}, [[[]]], true, false, null ] "
        val json = JsonReader.read(data)
        assert(json.toArray().map(JsonArray::size).getOrThrow() == 10)
        assert(json[0].toInt().getOrThrow() == 1)
        assert(json[1].toInt().getOrThrow() == 2)
        assert(json[2].toInt().getOrThrow() == 3)
        assert(json[3].toDouble().getOrThrow() == 4.4)
        assert(json[4].to_String().getOrThrow() == "a")
        assert(json[5].toObject().map(JsonObject::size).getOrThrow() == 0)
        assert(json[6].toArray().map(JsonArray::size).getOrThrow() == 1)
        assert(json[7].toBoolean().getOrThrow())
        assert(!json[8].toBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json[9].toNull().getOrThrow() == null)
    }

    @Test
    fun `should read array with all types`() {
        val data = " [ 1 , 2 , 3, \"a\", true, false, null, {\"a\": 1}, [1, 2, 3] ] "
        val json = JsonReader.read(data)
        val array = json.toArray()
        assert(array.map(JsonArray::size).getOrThrow() == 9)
        assert(array[0].toInt().getOrThrow() == 1)
        assert(array[1].toInt().getOrThrow() == 2)
        assert(array[2].toInt().getOrThrow() == 3)
        assert(array[3].to_String().getOrThrow() == "a")
        assert(array[4].toBoolean().getOrThrow())
        assert(!array[5].toBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(array[6].toNull().getOrThrow() == null)
        assert(array[7].getInt("a").getOrThrow() == 1)
        assert(array[8].toArray()[0].toInt().getOrThrow() == 1)
        assert(array[8].toArray()[1].toInt().getOrThrow() == 2)
        assert(array[8].toArray()[2].toInt().getOrThrow() == 3)
    }

    @Test
    fun `should read array in object`() {
        val data = "{ \"a\" : [ 1 , 2 , 3 ] }"
        val json = JsonReader.read(data)
        val array = json.getArray("a")
        assert(array.map(JsonArray::size).getOrThrow() == 3)
        assert(array[0].toInt().getOrThrow() == 1)
        assert(array[1].toInt().getOrThrow() == 2)
        assert(array[2].toInt().getOrThrow() == 3)
    }

    @Test
    fun `should read object`() {
        val data = "{\"a\": {\"b\": 1}}"
        val json = JsonReader.read(data)
        val obj = json.getObject("a")
        assert(obj.getInt("b").getOrThrow() == 1)
    }

    @Test
    fun `should read null`() {
        val data = "{\"a\": null}"
        val json = JsonReader.read(data)
        @Suppress("SENSELESS_COMPARISON")
        assert(json.getNull("a").getOrThrow() == null)
    }

    @Test
    fun `should read empty object`() {
        val data = "{}"
        val json = JsonReader.read(data)
        assert(json.getOrThrow() is JsonObject)
    }

    @Test
    fun `should empty array`() {
        val data = "[]"
        val json = JsonReader.read(data)
        assert(json.getOrThrow() is JsonArray)
    }

    @Test
    fun `should not read empty string`() {
        val data = JsonReader.read("") as? Either.Left
        assert(data?.value is JsonException)
    }

    @Test
    fun `should not read empty string with spaces`() {
        val data = JsonReader.read(" ") as? Either.Left
        assert(data?.value is JsonException)
    }

    @Test
    fun `should read empty string with spaces and newlines`() {
        val data = """
            {
                "a": "\n \t \"hola\" \t \n"
            }
        """.trimIndent()
        val json = JsonReader.read(data)
        assert(json.getString("a").getOrThrow() == "\n \t \"hola\" \t \n")
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
        assert(json.getInt("a").getOrThrow() == 1)
        assert(json.getString("b").getOrThrow() == "2")
        assert(json.getBoolean("c").getOrThrow())
        assert(!json.getBoolean("d").getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json.getNull("e").getOrThrow() == null)
        assert(json.getArray(" f ").map(JsonArray::size).getOrThrow() == 3)
        assert(json.getObject("g").getInt("h").getOrThrow() == 1)
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
        val result = JsonReader.read(data) as? Either.Left
        assert(result?.value is JsonException)
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
        val result = JsonReader.read(data) as? Either.Left
        assert(result?.value is JsonException)
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
        assert(json.getInt("a").getOrThrow() == 1)
        assert(json.getString("b").getOrThrow() == "2")
        assert(json.getBoolean("c").getOrThrow())
        assert(!json.getBoolean("d").getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json.getNull("e").getOrThrow() == null)
        assert(json.getArray("f").map(JsonArray::size).getOrThrow() == 3)
        assert(json.getObject("g").getInt("h").getOrThrow() == 1)
        assert(json.getObject("g").getObject("i").getInt("j").getOrThrow() == 1)

    }

    @Test
    fun `should json read from stream`() {
        val data = File("src/test/resources/photos.json").inputStream()
        val json = JsonReader.read(data).flatMap(JsonNode::toArray)
        assert(json.map(JsonArray::size).getOrThrow() == 5000)
        assert(json.getOrThrow().all { it["albumId"].isRightOrThrow() })
        assert(json.getOrThrow().all { it["id"].isRightOrThrow() })
        assert(json.getOrThrow().all { it["title"].isRightOrThrow() })
        assert(json.getOrThrow().all { it["url"].isRightOrThrow() })
        assert(json.getOrThrow().all { it["thumbnailUrl"].isRightOrThrow() })
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

        val json = JsonReader.read(data)
        assert(json.isRightOrThrow())
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

        val json = JsonReader.read(data) as? Either.Left
        assert(json?.value is JsonException)
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
        assert(json.getInt("a").getOrThrow() == 1)
        assert(json.getString("b").getOrThrow() == "2")
        assert(json.getBoolean("c").getOrThrow())
        assert(!json.getBoolean("d").getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json.getNull("e").getOrThrow() == null)
        assert(json.getArray("f").map(JsonArray::size).getOrThrow() == 3)
        assert(json["g"].getInt("h").getOrThrow() == 1)
        assert(json["g"]["i"]["j"].toInt().getOrThrow() == 1)
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

        val json = data.deserialize() as? Either.Left
        assert(json?.value is JsonException)
    }
}
