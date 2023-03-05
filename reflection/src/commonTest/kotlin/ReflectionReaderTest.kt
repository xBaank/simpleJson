import arrow.core.getOrElse
import simpleJson.reflection.JsonName
import simpleJson.reflection.deserializeFromString
import kotlin.test.Test


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

private data class MapWrapper(
    val map: Map<String, String>
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
    fun should_read_basic_values() {
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
    fun should_read_array() {
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
    fun should_read_array_of_class() {
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
    fun should_not_read_not_data_class_neither_array() {
        val json = """
            {
            "localDate": "2020-01-01"
            }
        """.trimIndent()

        val instance = deserializeFromString<MapWrapper>(json)
        assert(instance.isLeft())
    }

    @Test
    fun should_not_deserialize_value_with_incorrect_properties() {
        val json = """
            {
                "localDateee": "2020-01-01"
            }
        """.trimIndent()

        val instance = deserializeFromString<MapWrapper>(json)
        assert(instance.isLeft())
    }

    @Test
    fun should_not_read_not_data_class() {
        val json = """
            {
            }
        """.trimIndent()

        val instance = deserializeFromString<NotDataClass>(json)
        assert(instance.isLeft())
    }

    @Test
    fun should_not_assign_null() {
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
    fun should_read_with_annotation() {
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