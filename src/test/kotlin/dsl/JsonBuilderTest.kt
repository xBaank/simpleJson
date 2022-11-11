package dsl

import JsonNull
import getArrayOrNull
import getBooleanOrNull
import getJsonNullOrNull
import getNumberOrNull
import getStringOrNull
import org.junit.jupiter.api.Test
import toIntOrNull
import toNumberOrNull
import toStringOrNull

internal class JsonBuilderTest {

    @Test
    fun `should build json`() {
        val json = jObject {
            "a" to null
            "true" to true
            "false" to false
            4.3 to "b" //This is not part of builder, so it won't be added
            "b" to "c"
            "d" to jArray {
                add(jObject { "e" to "2"; "a" to 3 })
                add("f")
                add(1)
                add(5)
            }
        }

        assert(json.getJsonNullOrNull("a") == JsonNull)
        assert(json.getStringOrNull("b") == "c")
        assert(json.getBooleanOrNull("true") == true)
        assert(json.getBooleanOrNull("false") == false)
        val array = json.getArrayOrNull("d")
        assert(array?.size == 4)
        assert(array?.get(0)?.getNumberOrNull("a") == 3)
        assert(array?.get(0)?.getStringOrNull("e") == "2")
        assert(array?.get(1)?.toStringOrNull() == "f")
        assert(array?.get(2)?.toIntOrNull() == 1)
        assert(array?.get(3)?.toNumberOrNull() == 5)
    }

    @Test
    fun `should build json from array`() {
        val json = jArray {
            +jObject { "e" to "2"; "a" to 3 }
            +"f"
            +1
            add(5)
        }

        assert(json.size == 4)
        assert(json[0].getNumberOrNull("a") == 3)
        assert(json[0].getStringOrNull("e") == "2")
        assert(json[1].toStringOrNull() == "f")
        assert(json[2].toIntOrNull() == 1)
        assert(json[3].toNumberOrNull() == 5)
    }
}