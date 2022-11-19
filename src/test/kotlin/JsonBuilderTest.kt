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
                add(jObject {
                    "string" to "string"
                    "array" to jArray {
                        add("first")
                        add("second")
                    }
                })
                add("string")
                add(true)
                add(false)
                add(null)
                addArray { add("first") }
                addObject { "string" to "string" }
            }
            jArray("array2") { add(1) }
            "object" to jObject {}
            jObject("object2") {}
        }

        assert(json.getJsonNullOrNull("null") == JsonNull)
        assert(json.getBooleanOrNull("true") == true)
        assert(json.getBooleanOrNull("false") == false)
        assert(json.getDoubleOrNull("number") == 5.5)
        assert(json.getStringOrNull("string") == "string")
        assert(json.getArrayOrNull("array")?.getOrNull(0)?.toDoubleOrNull() == 1.2)
        assert(json.getArrayOrNull("array")?.getOrNull(1)?.toObjectOrNull()?.getStringOrNull("string") == "string")
        assert(
            json.getArrayOrNull("array")?.getOrNull(1)?.toObjectOrNull()?.getArrayOrNull("array")?.get(0)
                ?.toStringOrNull() == "first"
        )
        assert(
            json.getArrayOrNull("array")?.getOrNull(1)?.toObjectOrNull()?.getArrayOrNull("array")?.get(1)
                ?.toStringOrNull() == "second"
        )
        assert(json.getArrayOrNull("array")?.getOrNull(2)?.toStringOrNull() == "string")
        assert(json.getArrayOrNull("array")?.getOrNull(3)?.toBooleanOrNull() == true)
        assert(json.getArrayOrNull("array")?.getOrNull(4)?.toBooleanOrNull() == false)
        assert(json.getArrayOrNull("array")?.getOrNull(5)?.toJsonNullOrNull() == JsonNull)
        assert(json.getArrayOrNull("array")?.getOrNull(6)?.toArrayOrNull()?.getOrNull(0)?.toStringOrNull() == "first")
        assert(json.getArrayOrNull("array")?.getOrNull(7)?.toObjectOrNull()?.getStringOrNull("string") == "string")
        assert(json.getArrayOrNull("array2")?.getOrNull(0)?.toIntOrNull() == 1)
        assert(json.getObjectOrNull("object") != null)
        assert(json.getObjectOrNull("object2") != null)


    }

    @Test
    fun `should build json from array`() {

        val json = jArray {
            add(1.2)
            add(jObject {
                "string" to "string"
                "array" to jArray {
                    add("first")
                    add("second")
                }
            })
            add("string")
            add(true)
            add(false)
            add(null)
            addArray { add("first") }
            addObject { "string" to "string" }
            addAll(
                1.toJson(),
                "asd".toJson(),
                5L.toJson(),
                null.toJson(),
                true.toJson(),
                listOf(1.toJson(), 2.toJson()).toJson(),
                mapOf("a" to 1.toJson(), "b" to 2.toJson()).toJson()
            )
        }

        assert(json[0].toDoubleOrNull() == 1.2)
        assert(json[1].toObjectOrNull()?.getStringOrNull("string") == "string")
        assert(json[1].toObjectOrNull()?.getArrayOrNull("array")?.get(0)?.toStringOrNull() == "first")
        assert(json[1].toObjectOrNull()?.getArrayOrNull("array")?.get(1)?.toStringOrNull() == "second")
        assert(json[2].toStringOrNull() == "string")
        assert(json[3].toBooleanOrNull() == true)
        assert(json[4].toBooleanOrNull() == false)
        assert(json[5].toJsonNullOrNull() == JsonNull)
        assert(json[6].toArrayOrNull()?.getOrNull(0)?.toStringOrNull() == "first")
        assert(json[7].toObjectOrNull()?.getStringOrNull("string") == "string")
        assert(json[8].toIntOrNull() == 1)
        assert(json[9].toStringOrNull() == "asd")
        assert(json[10].toIntOrNull() == 5)
        assert(json[11].toJsonNullOrNull() == JsonNull)
        assert(json[12].toBooleanOrNull() == true)
        assert(json[13].toArrayOrNull()?.getOrNull(0)?.toIntOrNull() == 1)
        assert(json[13].toArrayOrNull()?.getOrNull(1)?.toIntOrNull() == 2)
        assert(json[14].toObjectOrNull()?.getIntOrNull("a") == 1)
        assert(json[14].toObjectOrNull()?.getIntOrNull("b") == 2)

    }
}