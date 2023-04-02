import arrow.core.Either
import arrow.core.getOrElse
import simpleJson.reflection.JsonName
import simpleJson.reflection.JsonSerializationException
import simpleJson.reflection.serializeToNode
import simpleJson.reflection.serializeToString
import kotlin.test.Test

private data class BasicTypes(
    val string: String,
    val number: Number,
    val boolean: Boolean,
    val nullable: String?,
    val list: List<Int>,
    val subType: SubType
)

private data class SubType(val localDate: String, val list: MutableList<String>)
private data class AnnotationWriter(@JsonName("name") val nameasdasd: String, @JsonName("number") val numberasdasd: Int)
class ReflectionWriterTest {
    @Test
    fun should_write_data_class() {
        val instance =
            BasicTypes("string", 5, true, null, listOf(1, 2, 3), SubType("2020-01-01", mutableListOf("1", "2", "3")))
        val json = serializeToString(instance).getOrElse { throw it }
        assert(json == """{"boolean":true,"list":[1,2,3],"nullable":null,"number":5,"string":"string","subType":{"list":["1","2","3"],"localDate":"2020-01-01"}}""")
    }

    @Test
    fun should_write_list() {
        val instance = listOf(1, 2, 3)
        val json = serializeToString(instance).getOrElse { throw it }
        assert(json == """[1,2,3]""")
    }

    @Test
    fun should_write_list_of_BasicTypes() {
        val instance = listOf(
            BasicTypes(
                "string",
                5,
                true,
                null,
                listOf(1, 2, 3),
                SubType("2020-01-01", mutableListOf("1", "2", "3"))
            )
        )
        val json = serializeToString(instance).getOrElse { throw it }
        assert(json == """[{"boolean":true,"list":[1,2,3],"nullable":null,"number":5,"string":"string","subType":{"list":["1","2","3"],"localDate":"2020-01-01"}}]""")
    }

    @Test
    fun should_not_write_unsupported_type() {
        val instance = mapOf(1 to 1)
        val result = serializeToNode(instance)
        assert(result is Either.Left<JsonSerializationException>)
    }

    @Test
    fun should_write_data_class_with_annotation() {
        val instance = AnnotationWriter("name", 5)
        val json = serializeToString(instance).getOrElse { throw it }
        assert(json == """{"name":"name","number":5}""")
    }
}