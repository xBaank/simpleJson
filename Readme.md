# simpleJson [![Java CI with Gradle](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml/badge.svg)](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml)

simpleJson is a data oriented JSON parser designed specifically for Kotlin multiplatform applications. Unlike other JSON
parsers that can be overly complex and difficult to work with, SimpleJson provides a simple and intuitive API that
allows developers to quickly and easily parse JSON data into strongly typed nodes.

To properly use it you need to have <a href="https://github.com/arrow-kt/arrow">Arrow</a>
and <a href="https://github.com/square/okio">Okio</a> as a dependency.

| Module                | Version                                                                                                                                                                  |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| simpleJson-core       | [![](https://img.shields.io/maven-central/v/io.github.xbaank/simpleJson-core)](https://central.sonatype.com/artifact/io.github.xbaank/simpleJson-core)             |
| simpleJson-reflection (DEPRECATED) | [![](https://img.shields.io/maven-central/v/io.github.xbaank/simpleJson-reflection)](https://central.sonatype.com/artifact/io.github.xbaank/simpleJson-reflection) |

## API

The main API consists of a bunch of extensions functions that allow you to serialize and deserialize json in a simple
way.

### Deserialize

To deserialize a json string into a JsonNode you can use the deserialized function.

```kotlin
val json = """ { "key" : "value" } """.deserialized()
```

You can also read it from a BufferedSource with the JsonReader function.

```kotlin
val path = "src/jvmTest/resources/photos.json"
val source: BufferedSource = FileSystem.SYSTEM.source(path.toPath()).buffer()
val json = source.deserialized()
```

### Serialize

To serialize a JsonNode into a json string you can use the serialized function.

```kotlin
val json = JsonArray(listOf(JsonString("value")))
val serialized = json.serialized()
val prettySerialized = json.serializedPretty(indent = "  ") 
```

You can also write it to a BufferedSink with the JsonWriter function.

```kotlin
val path = "src/jvmTest/resources/photos.json"
val source: BufferedSource = FileSystem.SYSTEM.source(path.toPath()).buffer()
val json = source.deserialized()
val sink: BufferedSink = FileSystem.SYSTEM.sink(path.toPath()).buffer()
json.serializedTo(sink)
json.serializedPrettyTo(sink, indent = "  ")
```

### Accessing data

To access the data you can use the get operator and the extension functions.

```kotlin
    val json = """ { "key" : "value" } """.deserialized()
    val value = json["key"].asString().getOrElse { "unknown" }
    //Gets the value of the key "key" and converts it to a string or a JsonException if it fails deserializing or finding the property.
    //If it fails it returns "unknown"
```

### Creating a json

To create a json you can use the DSL.

```kotlin
val json = jObject {
    "key" += "value"
    "key2" += 123
    "key3" += true
    "key4" += null
    "key5" += jObject {
        "key6" += "value"
    }
    "key7" += jArray("value1".asJson(), "value2".asJson(), "value3".asJson())
}
```

With all of these functions you can create and work with json in a simple way.

## Comparison with kotlinx.serialization

Let's start with a simple example. Imagine you want to access data from a json.
The common way would be to create a data class and deserialize the json into it.But creating a data class for every json
is not a good idea, It's even worse if you just access a few properties.

So, instead of creating a data class, you can use the JsonNode class to access the data dynamically .

Let's see how to do it with Kotlinx.serialization.

```kotlin
@Serializable
data class Info(val address: String, val phone: Int)

@Serializable
data class Photo(val name: String, val size: Int)

@Serializable
data class User(val name: String, val age: Int, val info: Info, val photos: List<Photo>)

val json = """
{
    "name" : "Juan",
    "age" : 20,
    "info": {
        "address" : "Mexico",
        "phone" : 1234567890
    }
    "photos" : [
        {
            "name" : "photo1",
            "size" : 100
        },
        {
            "name" : "photo2",
            "size" : 200
        }
    ]
}
"""

val address = try {
    Json.decodeFromString<User>(json).info.address
} catch (ex: Exception) {
    "unknown"
}
```

This would be the "common way" to do it, but it would add a bunch of unused and boilerplate code.

Now let's see how to do it with simpleJson.

```kotlin
val json = """
{
    "name" : "Juan",
    "age" : 20,
    "info": {
        "address" : "Mexico",
        "phone" : 1234567890
    }
    "photos" : [
        {
            "name" : "photo1",
            "size" : 100
        },
        {
            "name" : "photo2",
            "size" : 200
        }
    ]
}
""".deserialized()

// will return "unknown" if the key is not found or the value is not a string
// And you can still know why it failed because it returns an Either<JsonException, String>
// So it is completely safe.
val address = json["info"]["address"].asString().getOrElse { "unknown" }
```

As you can see, it's much simpler and easier to use and allows you to focus more on the
data received rather than the types or DTOs that need to be created.

