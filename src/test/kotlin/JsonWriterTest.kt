import org.junit.jupiter.api.Test

class JsonWriterTest {
    @Test
    fun `should write json`() {
        // TODO change builder to nodes?
        val json = jObject {
            "null" to null
            "true" to true
            "false" to false
            "number" to 5.5
            "string" to "string"
            "array" to jArray {
                add(1.2)
                addObj{
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
                addObj { "string" to "string" }
            }
            "object" to jObject {}
        }

        val string = JsonWriter.write(json)
        assert(string == """{"null":null,"true":true,"false":false,"number":5.5,"string":"string","array":[1.2,{"string":"string","array":["first","second"]},"string",true,false,null,["first"],{"string":"string"}],"object":{}}""")

    }
}