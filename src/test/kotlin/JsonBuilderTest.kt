import org.junit.jupiter.api.Test

internal class JsonBuilderTest {

    @Test
    fun `should build json`() {

        val json = jObject {
            "null" to null
            "true" to true
            "false" to false
            "number" to 5.5
            "string" to "string"
            "array" to jArray {
                add(1.2)
                jObject {
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
                add(jObject { "string" to "string" })
            }
            "object" to jObject {}
        }

        assert(json.getJsonNullOrNull("null") == JsonNull)
        assert(json.getBooleanOrNull("true") == true)
        assert(json.getBooleanOrNull("false") == false)
        assert(json.getDoubleOrNull("number") == 5.5)
        assert(json.getStringOrNull("string") == "string")
        assert(json.getArrayOrNull("array")?.get(0)?.toDoubleOrNull() == 1.2)
        assert(json.getArrayOrNull("array")?.get(1)?.toObjectOrNull()?.getStringOrNull("string") == "string")
        assert(json.getArrayOrNull("array")?.get(1)?.toObjectOrNull()?.getArrayOrNull("array")?.get(0)?.toStringOrNull() == "first")
        assert(json.getArrayOrNull("array")?.get(1)?.toObjectOrNull()?.getArrayOrNull("array")?.get(1)?.toStringOrNull() == "second")
        assert(json.getArrayOrNull("array")?.get(2)?.toStringOrNull() == "string")
        assert(json.getArrayOrNull("array")?.get(3)?.toBooleanOrNull() == true)
        assert(json.getArrayOrNull("array")?.get(4)?.toBooleanOrNull() == false)
        assert(json.getArrayOrNull("array")?.get(5)?.toJsonNullOrNull() == JsonNull)
        assert(json.getArrayOrNull("array")?.get(6)?.toArrayOrNull()?.get(0)?.toStringOrNull() == "first")
        assert(json.getArrayOrNull("array")?.get(7)?.toObjectOrNull()?.getStringOrNull("string") == "string")
        assert(json.getObjectOrNull("object") != null)


    }

    @Test
    fun `should build json from array`() {

        val json = jArray {
            add(1.2)
            jObject {
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
            add(jObject { "string" to "string" })
        }

        assert(json[0].toDoubleOrNull() == 1.2)
        assert(json[1].toObjectOrNull()?.getStringOrNull("string") == "string")
        assert(json[1].toObjectOrNull()?.getArrayOrNull("array")?.get(0)?.toStringOrNull() == "first")
        assert(json[1].toObjectOrNull()?.getArrayOrNull("array")?.get(1)?.toStringOrNull() == "second")
        assert(json[2].toStringOrNull() == "string")
        assert(json[3].toBooleanOrNull() == true)
        assert(json[4].toBooleanOrNull() == false)
        assert(json[5].toJsonNullOrNull() == JsonNull)
        assert(json[6].toArrayOrNull()?.get(0)?.toStringOrNull() == "first")
        assert(json[7].toObjectOrNull()?.getStringOrNull("string") == "string")

    }
}