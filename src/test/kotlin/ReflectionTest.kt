import org.junit.jupiter.api.Test
import simpleJson.reflection.deserialize
import java.time.LocalDate

data class Type(
    val name: String,
    val trueBoolean: Boolean,
    val falseBoolean: Boolean,
    val number: Int,
    val double: Double,
    val otherType: OtherType,
    val nullable: Int?
)

data class OtherType(
    val name: String,
    val numbers: List<Int> = emptyList(),
    val localDate: LocalDate
)

class ReflectionTest {
    @Test
    fun `should read basic values`() {
        val json = """
            {
                "name": "null",
                "trueBoolean": true,
                "falseBoolean": false,
                "number": 5,
                "double": 5.5,
                "otherType": {
                    "name": "null2",
                    "numbers": [1, 2, 3],
                    "localDate": "2020-01-01" 
                },
                "nullable": null
            }
        """.trimIndent()

        val instance = deserialize<Type>(json).getOrThrow()
        assert(instance.name == "null")
        assert(instance.trueBoolean)
        assert(!instance.falseBoolean)
        assert(instance.number == 5)
        assert(instance.double == 5.5)
        assert(instance.otherType.name == "null2")
        assert(instance.otherType.numbers.size == 3)
        assert(instance.otherType.numbers[0] == 1)
        assert(instance.otherType.numbers[1] == 2)
        assert(instance.otherType.numbers[2] == 3)
        assert(instance.nullable == null)
    }
}