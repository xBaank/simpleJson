import arrow.core.Either
import arrow.core.flatMap
import simpleJson.*
import simpleJson.exceptions.JsonEOFException
import simpleJson.exceptions.JsonParseException
import kotlin.test.Test

internal class JsonReaderTest {

    @Test
    fun should_read_unicode() {
        val data = "{\"a\": \"\\u00e9\"} "
        val json = data.deserialized()
        assert(json.flatMap { it.getString("a") }.getOrThrow() == "é")
    }

    @Test
    fun should_read_accent() {
        val data = """{"a": "ó"}"""
        val json = data.deserialized()
        assert(json.flatMap { it.getString("a") }.getOrThrow() == "ó")
    }

    @Test
    fun should_read_backslash() {
        val data = "{\"a\": \"\\\"\"} "
        val json = data.deserialized()
        assert(json.getString("a").getOrThrow() == "\"")
    }

    @Test
    fun should_read_slash() {
        val data = "{\"a\": \"\\/\"} "
        val json = data.deserialized()
        assert(json.getString("a").getOrThrow() == "/")
    }

    @Test
    fun should_read_backspace() {
        val data = "{\"a\": \"\b\"} "
        val json = data.deserialized()
        assert(json.getString("a").getOrThrow() == "\b")
    }

    @Test
    fun should_read_newline() {
        val data = "{\"a\": \"\n\"} "
        val json = data.deserialized()
        assert(json.getString("a").getOrThrow() == "\n")
    }

    @Test
    fun should_read_form_feed() {
        val data = "{\"a\": \"\u000c\"} "
        val json = data.deserialized()
        assert(json.getString("a").getOrThrow() == "\u000c")
    }

    @Test
    fun should_read_carriage_return() {
        val data = "{\"a\": \"\r\"} "
        val json = data.deserialized()
        assert(json.getString("a").getOrThrow() == "\r")
    }

    @Test
    fun should_read_tab() {
        val data = "{\"a\": \"\t\"} "
        val json = data.deserialized()
        assert(json.getString("a").getOrThrow() == "\t")
    }

    @Test
    fun should_read_number() {
        val data = "{\"a\": 1}"
        val json = data.deserialized()
        val number = json.getInt("a")
        assert(number.getOrThrow() == 1)
    }

    @Test
    fun should_read_number_with_minus() {
        val data = "{\"a\": -1}"
        val json = data.deserialized()
        val number = json.getInt("a")
        assert(number.getOrThrow() == (-1))
    }

    @Test
    fun should_read_number_with_plus() {
        val data = "{\"a\": +1}"
        val json = data.deserialized()
        val number = json.getInt("a")
        assert(number.getOrThrow() == 1)
    }

    @Test
    fun should_read_decimal_number() {
        val data = "{\"a\": 1.1}"
        val json = data.deserialized()
        val number = json.getDouble("a")
        assert(number.getOrThrow() == 1.1)
    }

    @Test
    fun should_read_decimal_number_with_minus() {
        val data = "{\"a\": -1.1}"
        val json = data.deserialized()
        val number = json.getDouble("a")
        assert(number.getOrThrow() == (-1.1))
    }

    @Test
    fun should_read_decimal_number_with_plus() {
        val data = "{\"a\": +1.1}"
        val json = data.deserialized()
        val number = json.getDouble("a")
        assert(number.getOrThrow() == 1.1)
    }

    @Test
    fun should_read_boolean() {
        val data = "{\"a\": true}"
        val json = data.deserialized()
        assert(json.getBoolean("a").getOrThrow())
    }

    @Test
    fun should_read_array() {
        val data = " [ 1 , 2 , 3,4.4,\"a\",{}, [[[]]], true, false, null ] "
        val json = data.deserialized()
        assert(json.asArray().map(JsonArray::size).getOrThrow() == 10)
        assert(json[0].asInt().getOrThrow() == 1)
        assert(json[1].asInt().getOrThrow() == 2)
        assert(json[2].asInt().getOrThrow() == 3)
        assert(json[3].asDouble().getOrThrow() == 4.4)
        assert(json[4].asString().getOrThrow() == "a")
        assert(json[5].asObject().map(JsonObject::size).getOrThrow() == 0)
        assert(json[6].asArray().map(JsonArray::size).getOrThrow() == 1)
        assert(json[7].asBoolean().getOrThrow())
        assert(!json[8].asBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json[9].asNull().getOrThrow() == null)
    }

    @Test
    fun should_read_array_with_all_types() {
        val data = " [ 1 , 2 , 3, \"a\", true, false, null, {\"a\": 1}, [1, 2, 3] ] "
        val json = data.deserialized()
        val array = json.asArray()
        assert(array.map(JsonArray::size).getOrThrow() == 9)
        assert(array[0].asInt().getOrThrow() == 1)
        assert(array[1].asInt().getOrThrow() == 2)
        assert(array[2].asInt().getOrThrow() == 3)
        assert(array[3].asString().getOrThrow() == "a")
        assert(array[4].asBoolean().getOrThrow())
        assert(!array[5].asBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(array[6].asNull().getOrThrow() == null)
        assert(array[7].getInt("a").getOrThrow() == 1)
        assert(array[8].asArray()[0].asInt().getOrThrow() == 1)
        assert(array[8].asArray()[1].asInt().getOrThrow() == 2)
        assert(array[8].asArray()[2].asInt().getOrThrow() == 3)
    }

    @Test
    fun should_read_array_in_object() {
        val data = "{ \"a\" : [ 1 , 2 , 3 ] }"
        val json = data.deserialized()
        val array = json.getArray("a")
        assert(array.map(JsonArray::size).getOrThrow() == 3)
        assert(array[0].asInt().getOrThrow() == 1)
        assert(array[1].asInt().getOrThrow() == 2)
        assert(array[2].asInt().getOrThrow() == 3)
    }

    @Test
    fun should_read_object() {
        val data = "{\"a\": {\"b\": 1}}"
        val json = data.deserialized()
        val obj = json.getObject("a")
        assert(obj.getInt("b").getOrThrow() == 1)
    }

    @Test
    fun should_read_null() {
        val data = "{\"a\": null}"
        val json = data.deserialized()
        @Suppress("SENSELESS_COMPARISON")
        assert(json.getNull("a").getOrThrow() == null)
    }

    @Test
    fun should_read_empty_object() {
        val data = "{}"
        val json = data.deserialized()
        assert(json.getOrThrow() is JsonObject)
    }

    @Test
    fun should_empty_array() {
        val data = "[]"
        val json = data.deserialized()
        assert(json.getOrThrow() is JsonArray)
    }

    @Test
    fun should_not_read_empty_string() {
        val data = "".deserialized() as? Either.Left
        assert(data?.value is JsonEOFException)
    }

    @Test
    fun should_not_read_empty_string_with_spaces() {
        val data = " ".deserialized() as? Either.Left
        assert(data?.value is JsonEOFException)
    }

    @Test
    fun should_read_empty_string_with_spaces_and_newlines() {
        val data = """
            {
                "a": "\n \t \"hola\" \t \n"
            }
        """.trimIndent()
        val json = data.deserialized()
        assert(json.getString("a").getOrThrow() == "\n \t \"hola\" \t \n")
    }

    @Test
    fun should_read_json() {
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
        val json = data.deserialized()
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
    fun should_not_read_json() {
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
        val result = data.deserialized() as? Either.Left
        assert(result?.value is JsonParseException)
    }

    @Test
    fun should_not_read_json_with_invalid_number() {
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
        val result = data.deserialized() as? Either.Left
        assert(result?.value is JsonParseException)
    }

    @Test
    fun should_read_json_with_nested_objects() {
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
        val json = data.deserialized()
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
    fun should_try_parse_json() {
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

        val json = data.deserialized()
        assert(json.isRightOrThrow())
    }

    @Test
    fun should_try_parse_json_with_exponent() {
        val data = """
            {
                "number": 5e-324
            }
        """.trimIndent()

        val json = data.deserialized()
        assert(json["number"].asDouble().getOrThrow() == 5E-324)
    }

    @Test
    fun should_try_parse_json_with_exponent_negative() {
        val data = """
            {
                "number": 1e-1
            }
        """.trimIndent()

        val json = data.deserialized()
        assert(json["number"].asDouble().getOrThrow() == 0.1)
    }

    @Test
    fun try_parse_should_return_null() {
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

        val json = data.deserialized() as? Either.Left
        assert(json?.value is JsonParseException)
    }

    @Test
    fun should_deserialize() {
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

        val json = data.deserialized()
        assert(json.getInt("a").getOrThrow() == 1)
        assert(json.getString("b").getOrThrow() == "2")
        assert(json.getBoolean("c").getOrThrow())
        assert(!json.getBoolean("d").getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json.getNull("e").getOrThrow() == null)
        assert(json.getArray("f").map(JsonArray::size).getOrThrow() == 3)
        assert(json["g"].getInt("h").getOrThrow() == 1)
        assert(json["g"]["i"]["j"].asInt().getOrThrow() == 1)
    }

    @Test
    fun should_not_deserialize() {
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

        val json = data.deserialized() as? Either.Left
        assert(json?.value is JsonParseException)
    }

    @Test
    fun should_encounter_eof() {
        val data = """
            {
                "a": 1
        """.trimIndent()

        val json = data.deserialized() as? Either.Left
        assert(json?.value is JsonEOFException)
    }
}
