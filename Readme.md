## simpleJson [![Java CI with Gradle](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml/badge.svg)](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml) [![](https://jitpack.io/v/xBaank/simpleJson.svg)](https://jitpack.io/#xBaank/simpleJson)

simpleJson is a simple json parser for the jvm made in kotlin.

### Deserialization

You can read using.

```kotlin
val json = JsonReader.read(""" { a : "a", b : [1 , "1"] } """) //will return either a JsonNode or a JsonException
```

or using the extension methods

```kotlin
val json = """ { a : "a", b : [1 , "1"] } """.deserialize() //will return either a JsonNode or a JsonException
```

You can also read from a stream such as a file.

```kotlin
val data = File("src/test/resources/photos.json").inputStream()
val json = JsonReader.read(data)
```

And specify the encoding too.

```kotlin
val data = File("src/test/resources/photos.json").inputStream()
val json = JsonReader.read(data, Charsets.UTF_32LE)
```

You can also use the `to` methods to cast to a type which will return either the type or a JsonException.

```kotlin
val json = JsonReader.read(data)
val name = json["name"].toString()
```

After reading, you can access the data using the `get` operator which will return either the corresponding JsonType or a JsonException.

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
val name = json.getString("name").orNull() //will return null if the key is not found or the value is not a string
val age = json.getInt("age").orNull()
val address = json["info"]["address"].toString().orNull() ?: "unknown" // will return "unknown" if the key is not found
val phone = json["info"]["phone"].toInt().getOrHandle { throw it } // will throw the exception if the key is not found or is not an int
val isPublic = json["photos"][0]["name"].toString().getOrHandle { throw it } 
//Throws the exception if property photos is not an array, it is not found, the index is out of bounds, name is not a string, or it is not found
```

### Serialization
You can serialize JsonNode to String

```kotlin
val jsonString = json.serialize()
```

or with a pretty print

```kotlin
val jsonString = json.serializePretty()
```

And to an output stream.

```kotlin
val stream = ByteArrayOutputStream()
JsonWriter(stream).write(json)
```
with pretty print

```kotlin
val stream = ByteArrayOutputStream()
JsonWriter(stream).prettyPrint().write(json)
```

### Dsl

The dsl/builder provides a safe way to create the json without the problem of adding a
wrong type that could cause a runtime error.

```kotlin
val json = jObject {
    "null" to null
    "true" to true
    "false" to false
    "number" to 5.5
    "string" to "string"
    "array" to jArray {
        add(1.2)
        add(jObject {
            "string" to "string"
            "array" to jArray {
                add("first")
                add("second")
            }
        })
        add("string")
        add(true)
        add(false)
        add(null)
        addArray { add("first") }
        addObject { "string" to "string" }
    }
    "object" to jObject {}
    jObject("object2") {}
}
```

## Gradle

Add jitpack

```kotlin
repositories {
    maven("https://jitpack.io")
}
```

Add dependency

```kotlin
dependencies {
    implementation("com.github.xBaank:simpleJson:7.0.0")
}

```

## Maven

Add jitpack

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add dependency

```xml

<dependency>
    <groupId>com.github.xBaank</groupId>
    <artifactId>simpleJson</artifactId>
    <version>7.0.0</version>
</dependency>
```



