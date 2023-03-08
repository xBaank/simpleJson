# simpleJson [![Java CI with Gradle](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml/badge.svg)](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml) 

simpleJson is a lightweight and versatile JSON parser designed specifically for Kotlin multiplatform applications. Unlike other JSON parsers that can be overly complex and difficult to work with, SimpleJson provides a simple and intuitive API that allows developers to quickly and easily parse JSON data into strongly typed nodes.

To properly use it you need to have <a href="https://github.com/arrow-kt/arrow">Arrow</a> and <a href="https://github.com/square/okio">Okio</a> as a dependency.


| Module                | Version                                                                                                                                                                  |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| simpleJson-core       | [![](https://img.shields.io/maven-central/v/io.github.xbaank/simpleJson-core)](https://central.sonatype.com/artifact/io.github.xbaank/simpleJson-core/1.0.0)             |
| simpleJson-reflection | [![](https://img.shields.io/maven-central/v/io.github.xbaank/simpleJson-reflection)](https://central.sonatype.com/artifact/io.github.xbaank/simpleJson-reflection/1.0.0) |


## Comparison with kotlinx.serialization
Let's start with a simple example. Imagine you want to access data from a json. 
The common way would be to create a data class and deserialize the json into it.

But creating a data class for every json is not a good idea, It's even worse if you just access a few properties.

So, instead of creating a data class, you can use the JsonNode class to access the data dynamically.

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
}
catch (ex : Exception) {
    "unknown"
}
```

This would be the "common way" to do it, but there are other ways to do it.

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
""".deserialize()

// will return "unknown" if the key is not found or the value is not a string
// And you can still know why it failed because it returns an Either<JsonException, String>
// So it is completely safe.
val address = json["info"]["address"].asString().getOrNull() ?: "unknown"
```

As you can see, it's much simpler and easier to use and allows you to focus on the more on the 
data received rather than the types or DTOs that need to be created.

You can still deserialize the json into a JsonElement with kotlinx.serialization.

But it won't be as easy to access the data because kotlinx.serialization deserializes the json into a JsonObject, JsonArray and JsonPrimitive,
making no distinction (at the time it is being parsed) between a string, a number, a boolean or a null (The main reason is that this improves speed when deserializing).

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
"""

val address = try {
    //Here jsonObject can throw and jsonPrimitive can throw, 
    //and you don't know what type is jsonPrimitive, it can be null, it can be bool, it can be a number, it can be a string.
    Json.decodeFromString<JsonElement>(json).jsonObject["info"]?.jsonObject["address"]?.jsonPrimitive?.content
}
catch (ex : Exception) {
    "unknown"
}
```

## Deserialization
You can deserialize a json string into a JsonNode with the deserialize function.

```kotlin
    val json = """
    {
        "key" : "value"
    }
    """.deserialize()
```

Or you can read it from a BufferedSource with the JsonReader function.
```kotlin
    val path = "src/jvmTest/resources/photos.json"
    val source: BufferedSource = FileSystem.SYSTEM.source(path.toPath()).buffer()
    val json = JsonReader.read(source).asArray()
```


## DSL

The DSL provides a safe way to create the json without the problem of adding a
wrong type that could cause a runtime error.

```kotlin
val json = jObject {
    "null" += null
    "true" += true
    "false" += false
    "number" += 5.5
    "string" += "string"
    "array" += jArray {
        add(1.2)
        add(jObject {
            "string" += "string"
            "array" += jArray {
                add("first")
                add("second")
            }
        })
        add("string")
        add(true)
        add(false)
        add(null)
        addArray { add("first") }
        addObject { "string" += "string" }
    }
    "object" += jObject {} //empty object
}
```

## Serialization

With the json created via dsl, you can serialize it to a string.

You can serialize JsonNode to String

```kotlin
val jsonString = json.serialize()
```

With a pretty print

```kotlin
val jsonString = json.serializePretty()
```

And to an BufferedSink.

```kotlin
val stream = Buffer()
JsonWriter(stream).write(node)
return stream.readUtf8()
```

With pretty print

```kotlin
val stream = Buffer()
JsonWriter(stream).prettyPrint().write(node)
return stream.readUtf8()
```



## Reflection
There is a module to use reflection deserialize objects into data classes.

It is only available for JVM and supports only primitive types, lists, and data classes.
```kotlin
val json = """
    [
        1,
        2,
        3
    ]
""".trimIndent()

val instance = deserialize<List<Int>>(json).getOrElse { throw it }
```

And to serialize objects into json.

```kotlin
val json = serialize(listOf(1, 2, 3))
//json is "[1,2,3]"
```



