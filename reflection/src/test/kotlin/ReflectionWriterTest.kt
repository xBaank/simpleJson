import arrow.core.Either
import arrow.core.getOrElse
import org.junit.jupiter.api.Test
import simpleJson.exceptions.JsonException
import simpleJson.reflection.serializeToNode
import simpleJson.reflection.serializeToString
import java.time.LocalDate

private data class BasicTypes(val string: String, val number: Number,val boolean: Boolean, val nullable : String?, val list: List<Int>, val subType: SubType)
private data class SubType(val localDate: String, val list: MutableList<String>)
class ReflectionWriterTest {
    @Test
    fun `should write data class`() {
        val instance = BasicTypes("string", 5, true, null, listOf(1,2,3), SubType("2020-01-01", mutableListOf("1", "2", "3")))
        val json = serializeToString(instance).getOrElse { throw it }
        assert(json == """{"boolean":true,"list":[1,2,3],"nullable":null,"number":5,"string":"string","subType":{"list":["1","2","3"],"localDate":"2020-01-01"}}""")
    }

    @Test
    fun `should write list`() {
        val instance = listOf(1,2,3)
        val json = serializeToString(instance).getOrElse { throw it }
        assert(json == """[1,2,3]""")
    }

    @Test
    fun `should write list of BasicTypes`() {
        val instance = listOf(BasicTypes("string", 5, true, null, listOf(1,2,3), SubType("2020-01-01", mutableListOf("1", "2", "3"))))
        val json = serializeToString(instance).getOrElse { throw it }
        assert(json == """[{"boolean":true,"list":[1,2,3],"nullable":null,"number":5,"string":"string","subType":{"list":["1","2","3"],"localDate":"2020-01-01"}}]""")
    }

    @Test
    fun `should not write unsupported type`() {
        val instance = LocalDate.now()
        val result = serializeToNode(instance)
        assert(result is Either.Left<JsonException>)
    }
}