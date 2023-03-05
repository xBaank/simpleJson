import simpleJson.*
import kotlin.test.Test

class OperatorTests {
    @Test
    fun should_create_json_and_then_change_it() {
        val json = jObject {
            "a" += 1
        } as JsonNode

        json["a"] = 2
        assert(json["a"].asInt().getOrThrow() == 2)
    }
    @Test
    fun should_create_json_and_then_change_object_in_it() {
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
    fun should_create_json_and_then_add_object_in_it() {
        val json = jObject {
            "a" += 1
        } as JsonNode

        json["b"] = 2

        assert(json["b"].asInt().getOrThrow() == 2)
    }
}