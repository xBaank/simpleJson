import arrow.core.getOrElse
import org.junit.jupiter.api.Test
import simpleJson.reflection.serialize

private data class BasicTypes(val string: String, val number: Number)
class ReflectionWriterTest {
    @Test
    fun `should write data class`() {
        val instance = BasicTypes("string", 5)
        val json = serialize(instance).getOrElse { throw it }
        assert(json == """
            {
                "localDate": "2020-01-01",
                "nullable": null
            }
        """.trimIndent())
    }
}