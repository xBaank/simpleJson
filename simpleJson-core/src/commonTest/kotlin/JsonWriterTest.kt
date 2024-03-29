import simpleJson.*
import kotlin.test.Test

class JsonWriterTest {
    @Test
    fun should_write_json() {
        val json = jObject {
            "null" to null
            "true" to true
            "false" to false
            "number" to 5.5
            "string" to "string"
            "array" to jArray {
                add(1.2)
                addObject {
                    "string" to "string"
                    "array" to jArray {
                        add("first")
                        add("second")
                    }
                }
                add("string")
                add(true)
                add(false)
                add(null)
                add(jArray { add("first") })
                addObject { "string" to "string" }
            }
            "object" to jObject {}
        }

        val string = json.serialized()
        assert(string == """{"null":null,"true":true,"false":false,"number":5.5,"string":"string","array":[1.2,{"string":"string","array":["first","second"]},"string",true,false,null,["first"],{"string":"string"}],"object":{}}""")
    }

    @Test
    fun should_write_json_and_read_it_again() {
        val json = jObject {
            "null" to null
            "true" to true
            "false" to false
            "number" to 5.5
            "string" to "string"
            "array" to jArray {
                add(1.2)
                addObject {
                    "string" to "string"
                    "array" to jArray {
                        add("first")
                        add("second")
                    }
                }
                add("string")
                add(true)
                add(false)
                add(null)
                add(jArray { add("first") })
                addObject { "string" to "string" }
            }
            "object" to jObject {}
        }

        val string = json.serialized()
        val jsonAgain = string.deserialized().getOrThrow()
        assert(json == jsonAgain)
    }

    @Test
    fun should_write_json_with_indent_and_read_it_again() {
        val json = jObject {
            "null" to null
            "true" to true
            "false" to false
            "number" to 5.5
            "string" to "string"
            "array" to jArray {
                add(1.2)
                addObject {
                    "string" to "string"
                    "array" to jArray {
                        add("first")
                        add("second")
                    }
                }
                add("string")
                add(true)
                add(false)
                add(null)
                add(jArray { add("first") })
                addObject { "string" to "string" }
            }
            "object" to jObject {}
        }

        val string = json.serializedPretty()
        val jsonAgain = string.deserialized().getOrThrow()
        assert(json == jsonAgain)
    }

    @Test
    fun should_write_json_with_escaped_characters() {
        val json = jObject {
            "string" to " \" \\ / \b \u000c \n \r \t \n "
        }

        val string = json.serialized()
        assert(string == """{"string":" \" \\ / \b \f \n \r \t \n "}""")
    }
}
