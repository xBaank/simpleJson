import org.junit.jupiter.api.Test
import simpleJson.*

class JsonWriterTest {
    @Test
    fun `should write json`() {
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

        val string = json.serialize()
        assert(string == """{"null":null,"true":true,"false":false,"number":5.5,"string":"string","array":[1.2,{"string":"string","array":["first","second"]},"string",true,false,null,["first"],{"string":"string"}],"object":{}}""")
    }

    @Test
    fun `should write json and read it again`() {
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

        val string = json.serialize()
        val jsonAgain = string.deserialize().getOrThrow()
        assert(json == jsonAgain)
    }

    @Test
    fun `should write json with indent and read it again`() {
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

        val string = json.serializePretty()
        val jsonAgain = string.deserialize().getOrThrow()
        assert(json == jsonAgain)
    }

    @Test
    fun `should write json with escaped characters`() {
        val json = jObject {
            "string" to " \" \\ / \b \u000c \n \r \t \n "
        }

        val string = json.serialize()
        assert(string == """{"string":" \" \\ / \b \f \n \r \t \n "}""")
    }
}
