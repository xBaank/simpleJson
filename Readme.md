## simpleJson [![Java CI with Gradle](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml/badge.svg)](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml) [![](https://jitpack.io/v/xBaank/simpleJson.svg)](https://jitpack.io/#xBaank/simpleJson)

simpleJson is a simple json parser for the jvm made in kotlin.

### Usage

You can read using.

```kotlin
val json = JsonReader.readOrNull(""" { a : "a", b : [1 , "1"] } """) //will return null if data is not a valid json
val json = JsonReader.read(""" { a : "a", b : [1 , "1"] } """) //will throw an exception if data is not a valid json
```

or

```kotlin
val json = """ { a : "a", b : [1 , "1"] } """.deserializeOrNull() //will return null if data is not a valid json
val json = """ { a : "a", b : [1 , "1"] } """.deserialize() //will throw an exception if data is not a valid json
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

After reading, you can access the data using the `getOrNull` methods which will return null if the key is not found or
the
value is not of the correct type.

```kotlin
val json = JsonReader.read(data)
val name = json.getStringOrNull("name")
val age = json.getIntOrNull("age")
val infoName = json.getObjectOrNull("info")?.getStringOrNull("name") ?: "unknown"
val isPublic =
    json.getArrayOrNull("photos")?.getOrNull(0)?.getBooleanOrNull("isPublic") ?: throw Exception("isPublic not found")
```

You can also use the `toOrNull` methods which will return null if the value is not of the correct type.

```kotlin
val json = JsonReader.read(data)
val name = json.getPropertyOrNull("name")?.toStringOrNull()
```

## Dsl

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

You can serialize it to string

```kotlin
val jsonString = json.serialize()
```

or

```kotlin
val jsonString = json.serializePretty()
```

And to an output stream.

```kotlin
val stream = ByteArrayOutputStream()
JsonWriter(stream).write(json)
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
    implementation("com.github.xBaank:simpleJson:5.0.1")
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
    <version>5.0.1</version>
</dependency>
```



