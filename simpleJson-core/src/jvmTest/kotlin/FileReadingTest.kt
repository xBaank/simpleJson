import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import org.junit.Test
import simpleJson.asArray
import simpleJson.deserialized
import simpleJson.get

class FileReadingTest {
    @Test
    fun should_read_json_from_file() {
        val path = "src/jvmTest/resources/photos.json"
        val source: BufferedSource = FileSystem.SYSTEM.source(path.toPath()).buffer()
        val json = source.deserialized().asArray()
        assert(json.getOrThrow().size == 5000)
        assert(json.getOrThrow().all { it["albumId"].isRightOrThrow() })
        assert(json.getOrThrow().all { it["id"].isRightOrThrow() })
        assert(json.getOrThrow().all { it["title"].isRightOrThrow() })
        assert(json.getOrThrow().all { it["url"].isRightOrThrow() })
        assert(json.getOrThrow().all { it["thumbnailUrl"].isRightOrThrow() })
    }
}