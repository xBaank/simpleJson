import org.junit.jupiter.api.Test
import simpleJson.reflection.deserialize

data class Type(val name: String, val type: Boolean, val number: Int, val double: Double, val otherType: OtherType)
data class OtherType(val name: String)
class ReflectionTest {
    @Test
    fun test() {
        val json = """
            {
                "name": "null",
                "type": true,
                "number": 5,
                "double": 5.5,
                "otherType": {
                    "name": "null2"
                }
            }
        """.trimIndent()

        val instance = deserialize<Type>(json)
    }
}