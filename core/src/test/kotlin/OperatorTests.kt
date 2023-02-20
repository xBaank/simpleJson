import org.junit.jupiter.api.Test
import simpleJson.*

class OperatorTests {
    @Test
    fun `should create json and then change it`() {
        val json = jObject {
            "a" += 1
        } as JsonNode

        json["a"] = 2
        assert(json["a"].asInt().getOrThrow() == 2)
    }
    @Test
    fun `should create json and then change object in it`() {
        val json = jObject {
            "a" += jObject {
                "b" += 1
            }
        } as JsonNode

        val data = json["a"].asObject()
        data["b"] = 2

        assert(json["a"]["b"].asInt().getOrThrow() == 2)
    }

    @Test
    fun `should create json and then add object in it`() {
        val json = jObject {
            "a" += 1
        } as JsonNode

        json["b"] = 2

        assert(json["b"].asInt().getOrThrow() == 2)
    }
}