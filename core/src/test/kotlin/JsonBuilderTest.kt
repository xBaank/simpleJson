import org.junit.jupiter.api.Test
import simpleJson.*

internal class JsonBuilderTest {

    @Test
    fun `should build json`() {

        val json = jObject {
            "null" += null
            "true" += true
            "false" += false
            "number" += 5.5
            "string" += "string"
            "array" += jArray {
                add(1.2)
                add(jObject {
                    "string" += "string"
                    "array" += jArray {
                        add("first")
                        add("second")
                    }
                })
                add("string")
                add(true)
                add(false)
                add(null)
                addArray { add("first") }
                addObject { "string" += "string" }
                +"asd"
                +jArray { +"first" }
                +null
                +false
                +jObject { "string" += "string" }
                +(1 as Number) //or
                +2.toJson()
            }
            "array2" to jArray {
                add(1)
            }
            "object" to jObject {}
            "object2" to jObject {}
            "object3" += jArray(1.toJson(), null.toJson())
        } as JsonNode

        @Suppress("SENSELESS_COMPARISON")
        assert(json.getNull("null").getOrThrow() == null)
        assert(json.getBoolean("true").getOrThrow())
        assert(!json.getBoolean("false").getOrThrow())
        assert(json.getDouble("number").getOrThrow() == 5.5)
        assert(json.getString("string").getOrThrow() == "string")
        assert(json["array"][0].toDouble().getOrThrow() == 1.2)
        assert(json["array"][1].getString("string").getOrThrow() == "string")
        assert(json["array"][1]["array"][0].to_String().getOrThrow() == "first")
        assert(json["array"][1]["array"][1].to_String().getOrThrow() == "second")
        assert(json["array"][2].to_String().getOrThrow() == "string")
        assert(json["array"][3].toBoolean().getOrThrow())
        assert(!json["array"][4].toBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json["array"][5].toNull().getOrThrow() == null)
        assert(json["array"][6][0].to_String().getOrThrow() == "first")
        assert(json["array"][7].toObject().getString("string").getOrThrow() == "string")
        assert(json["array"][8].to_String().getOrThrow() == "asd")
        assert(json["array"][9][0].to_String().getOrThrow() == "first")
        @Suppress("SENSELESS_COMPARISON")
        assert(json["array"][10].toNull().getOrThrow() == null)
        assert(!json["array"][11].toBoolean().getOrThrow())
        assert(json["array"][12].toObject().getString("string").getOrThrow() == "string")
        assert(json["array"][13].toDouble().getOrThrow() == 1.0)
        assert(json["array"][14].toDouble().getOrThrow() == 2.0)
        assert(json["array2"][0].toInt().getOrThrow() == 1)
        assert(json["object"].isRightOrThrow())
        assert(json["object2"].isRightOrThrow())
        assert(json["object3"][0].toInt().getOrThrow() == 1)
        @Suppress("SENSELESS_COMPARISON")
        assert(json["object3"][1].toNull().getOrThrow() == null)


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

        assert(json[0].toDouble().getOrThrow() == 1.2)
        assert(json[1].getString("string").getOrThrow() == "string")
        assert(json[1]["array"][0].to_String().getOrThrow() == "first")
        assert(json[1]["array"][1].to_String().getOrThrow() == "second")
        assert(json[2].to_String().getOrThrow() == "string")
        assert(json[3].toBoolean().getOrThrow())
        assert(!json[4].toBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json[5].toNull().getOrThrow() == null)
        assert(json[6][0].to_String().getOrThrow() == "first")
        assert(json[7].getString("string").getOrThrow() == "string")
        assert(json[8].toInt().getOrThrow() == 1)
        assert(json[9].to_String().getOrThrow() == "asd")
        assert(json[10].toLong().getOrThrow() == 5L)
        @Suppress("SENSELESS_COMPARISON")
        assert(json[11].toNull().getOrThrow() == null)
        assert(json[12].toBoolean().getOrThrow())
        assert(json[13][0].toInt().getOrThrow() == 1)
        assert(json[13][1].toInt().getOrThrow() == 2)
        assert(json[14]["a"].toInt().getOrThrow() == 1)

    }

    @Test
    fun `should create json and then change it`() {
        val json = jObject {
            "a" to 1.toJson()
        } as JsonNode

        json["a"] = 2
        assert(json["a"].toInt().getOrThrow() == 2)
    }
}