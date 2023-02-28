import arrow.core.getOrElse
import org.junit.jupiter.api.Test
import simpleJson.reflection.JsonName
import simpleJson.reflection.deserializeFromString
import java.time.LocalDate


private data class Type(
    val name: String,
    val trueBoolean: Boolean,
    val falseBoolean: Boolean,
    val number: Int,
    val double: Double,
    val otherType: OtherType,
    val nullable: Int?
)

private data class OtherType(
    val name: String,
    val numbers: List<Int>,
    val nullableString : String?,
    val localDate: String
)

private data class LocalDateWrapper(
    val localDate: LocalDate
)

private class NotDataClass

private data class AnnotationTest(
    @JsonName("name")
    val nameasdasd: String,
    @JsonName("number")
    val numberasdasd: Int
)


class ReflectionReaderTest {
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
                    "nullableString": null,
                    "localDate": "2020-01-01" 
                },
                "nullable": null
            }
        """.trimIndent()

        val instance = deserializeFromString<Type>(json).getOrElse { throw it }
        assert(instance.name == "null")
        assert(instance.trueBoolean)
        assert(!instance.falseBoolean)
        assert(instance.number == 5)
        assert(instance.double == 5.5)
        assert(instance.otherType.name == "null2")
        assert(instance.otherType.numbers.count() == 3)
        assert(instance.otherType.numbers.toList()[0] == 1)
        assert(instance.otherType.numbers.toList()[1] == 2)
        assert(instance.otherType.numbers.toList()[2] == 3)
        assert(instance.otherType.nullableString == null)
        assert(instance.otherType.localDate == "2020-01-01")
        assert(instance.nullable == null)
    }

    @Test
    fun `should read array`() {
        val json = """
            [
                1,
                2.1,
                3
            ]
        """.trimIndent()

        val instance = deserializeFromString<List<Int>>(json).getOrElse { throw it }
        assert(instance.size == 3)
        assert(instance[0] == 1)
        assert(instance[1] == 2)
        assert(instance[2] == 3)
    }

    @Test
    fun `should read array of class`() {
        val json = """
            [
                {
                    "name": "null",
                    "trueBoolean": true,
                    "falseBoolean": false,
                    "number": 5,
                    "double": 5.5,
                    "otherType": {
                        "name": "null2",
                        "numbers": [1, 2, 3],
                        "nullableString": null,
                        "localDate": "2020-01-01" 
                    },
                    "nullable": null
                }
            ]
        """.trimIndent()

        val instance = deserializeFromString<List<Type>>(json).getOrElse { throw it }
        assert(instance.size == 1)
        assert(instance[0].name == "null")
        assert(instance[0].trueBoolean)
        assert(!instance[0].falseBoolean)
        assert(instance[0].number == 5)
        assert(instance[0].double == 5.5)
        assert(instance[0].otherType.name == "null2")
        assert(instance[0].otherType.numbers.count() == 3)
        assert(instance[0].otherType.numbers.toList()[0] == 1)
        assert(instance[0].otherType.numbers.toList()[1] == 2)
        assert(instance[0].otherType.numbers.toList()[2] == 3)
        assert(instance[0].otherType.nullableString == null)
        assert(instance[0].otherType.localDate == "2020-01-01")
        assert(instance[0].nullable == null)
    }

    @Test
    fun `should not read not data class neither array`() {
        val json = """
            {
            "localDate": "2020-01-01"
            }
        """.trimIndent()

        val instance = deserializeFromString<LocalDateWrapper>(json)
        assert(instance.isLeft())
    }

    @Test
    fun `should not deserialize value with incorrect properties`() {
        val json = """
            {
                "localDateee": "2020-01-01"
            }
        """.trimIndent()

        val instance = deserializeFromString<LocalDateWrapper>(json)
        assert(instance.isLeft())
    }

    @Test
    fun `should not read not data class`() {
        val json = """
            {
            }
        """.trimIndent()

        val instance = deserializeFromString<NotDataClass>(json)
        assert(instance.isLeft())
    }

    @Test
    fun `should not assign null`() {
        val json = """
            {
                "name": "null",
                "trueBoolean": true,
                "falseBoolean": false,
                "number": 5,
                "double": 5.5,
                "otherType": {
                    "name": null,
                    "numbers": [1, 2, 3],
                    "nullableString": null,
                    "localDate": "2020-01-01" 
                },
                "nullable": null
            }
        """.trimIndent()

        val instance = deserializeFromString<Type>(json)
        assert(instance.isLeft())
    }

    @Test
    fun `should read with annotation`() {
        val json = """
            {
                "name": "null",
                "number": 5
            }
        """.trimIndent()

        val instance = deserializeFromString<AnnotationTest>(json).getOrElse { throw it }
        assert(instance.nameasdasd == "null")
        assert(instance.numberasdasd == 5)
    }
}