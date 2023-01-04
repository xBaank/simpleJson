import org.junit.jupiter.api.Test
import simpleJson.*

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

        @Suppress("SENSELESS_COMPARISON")
        assert(json.getNull("null").getOrThrow() == null)
        assert(json.getBoolean("true").getOrThrow())
        assert(!json.getBoolean("false").getOrThrow())
        assert(json.getDouble("number").getOrThrow() == 5.5)
        assert(json.getString("string").getOrThrow() == "string")
        assert(json.getArray("array")[0].toDouble().getOrThrow() == 1.2)
        assert(json.getArray("array")[1].toObject().getString("string").getOrThrow() == "string")
        assert(
            json.getArray("array")[1].toObject().getArray("array")[0]
                .to_String().getOrThrow() == "first"
        )
        assert(
            json.getArray("array")[1].toObject().getArray("array")[1]
                .to_String().getOrThrow() == "second"
        )
        assert(json.getArray("array")[2].to_String().getOrThrow() == "string")
        assert(json.getArray("array")[3].toBoolean().getOrThrow())
        assert(!json.getArray("array")[4].toBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json.getArray("array")[5].toNull().getOrThrow() == null)
        assert(json.getArray("array")[6].toArray()[0].to_String().getOrThrow() == "first")
        assert(json.getArray("array")[7].toObject().getString("string").getOrThrow() == "string")
        assert(json.getArray("array2")[0].toInt().getOrThrow() == 1)
        assert(json.getObject("object").isRightOrThrow())
        assert(json.getObject("object2").isRightOrThrow())


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
        assert(json[1].toObject().getString("string").getOrThrow() == "string")
        assert(json[1].toObject().getArray("array")[0].to_String().getOrThrow() == "first")
        assert(json[1].toObject().getArray("array")[1].to_String().getOrThrow() == "second")
        assert(json[2].to_String().getOrThrow() == "string")
        assert(json[3].toBoolean().getOrThrow())
        assert(!json[4].toBoolean().getOrThrow())
        @Suppress("SENSELESS_COMPARISON")
        assert(json[5].toNull().getOrThrow() == null)
        assert(json[6].toArray()[0].to_String().getOrThrow() == "first")
        assert(json[7].toObject().getString("string").getOrThrow() == "string")
        assert(json[8].toInt().getOrThrow() == 1)
        assert(json[9].to_String().getOrThrow() == "asd")
        assert(json[10].toLong().getOrThrow() == 5L)
        @Suppress("SENSELESS_COMPARISON")
        assert(json[11].toNull().getOrThrow() == null)
        assert(json[12].toBoolean().getOrThrow())
        assert(json[13].toArray()[0].toInt().getOrThrow()== 1)
        assert(json[13].toArray()[1].toInt().getOrThrow() == 2)
        assert(json[14].toObject()["a"].toInt().getOrThrow() == 1)

    }
}