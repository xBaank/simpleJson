import simpleJson.*
import kotlin.test.Test

internal class JsonBuilderTest {

    @Test
    fun should_build_json() {

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
                +2.asJson()
            }
            "array2" to jArray {
                add(1)
            }
            "object" to jObject {}
            "object2" to jObject {}
            "object3" += jArray(1.asJson(), null.asJson())
        } as JsonNode

        @Suppress("SENSELESS_COMPARISON")
        assert(json.getNull("null").getOrThrow() == null)
        assert(json.getBoolean("true").getOrThrow())
        assert(!json.getBoolean("false").getOrThrow())
        assert(json.getDouble("number").getOrThrow() == 5.5)
        assert(json.getString("string").getOrThrow() == "string")
        assert(json["array"][0].asDouble().getOrThrow() == 1.2)
        assert(json["array"][1].getString("string").getOrThrow() == "string")
        assert(json["array"][1]["array"][0].asString().getOrThrow() == "first")
        assert(json["array"][1]["array"][1].asString().getOrThrow() == "second")
        assert(json["array"][2].asString().getOrThrow() == "string")
        assert(json["array"][3].asBoolean().getOrThrow())
        assert(!json["array"][4].asBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json["array"][5].asNull().getOrThrow() == null)
        assert(json["array"][6][0].asString().getOrThrow() == "first")
        assert(json["array"][7].asObject().getString("string").getOrThrow() == "string")
        assert(json["array"][8].asString().getOrThrow() == "asd")
        assert(json["array"][9][0].asString().getOrThrow() == "first")
        @Suppress("SENSELESS_COMPARISON")
        assert(json["array"][10].asNull().getOrThrow() == null)
        assert(!json["array"][11].asBoolean().getOrThrow())
        assert(json["array"][12].asObject().getString("string").getOrThrow() == "string")
        assert(json["array"][13].asDouble().getOrThrow() == 1.0)
        assert(json["array"][14].asDouble().getOrThrow() == 2.0)
        assert(json["array2"][0].asInt().getOrThrow() == 1)
        assert(json["object"].isRightOrThrow())
        assert(json["object2"].isRightOrThrow())
        assert(json["object3"][0].asInt().getOrThrow() == 1)
        @Suppress("SENSELESS_COMPARISON")
        assert(json["object3"][1].asNull().getOrThrow() == null)


    }

    @Test
    fun should_build_json_from_array() {

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
                1.asJson(),
                "asd".asJson(),
                5L.asJson(),
                null.asJson(),
                true.asJson(),
                listOf(1.asJson(), 2.asJson()).asJson(),
                mapOf("a" to 1.asJson(), "b" to 2.asJson()).asJson()
            )
        }

        assert(json[0].asDouble().getOrThrow() == 1.2)
        assert(json[1].getString("string").getOrThrow() == "string")
        assert(json[1]["array"][0].asString().getOrThrow() == "first")
        assert(json[1]["array"][1].asString().getOrThrow() == "second")
        assert(json[2].asString().getOrThrow() == "string")
        assert(json[3].asBoolean().getOrThrow())
        assert(!json[4].asBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json[5].asNull().getOrThrow() == null)
        assert(json[6][0].asString().getOrThrow() == "first")
        assert(json[7].getString("string").getOrThrow() == "string")
        assert(json[8].asInt().getOrThrow() == 1)
        assert(json[9].asString().getOrThrow() == "asd")
        assert(json[10].asLong().getOrThrow() == 5L)
        @Suppress("SENSELESS_COMPARISON")
        assert(json[11].asNull().getOrThrow() == null)
        assert(json[12].asBoolean().getOrThrow())
        assert(json[13][0].asInt().getOrThrow() == 1)
        assert(json[13][1].asInt().getOrThrow() == 2)
        assert(json[14]["a"].asInt().getOrThrow() == 1)

    }


}