## simpleJson [![Java CI with Gradle](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml/badge.svg)](https://github.com/xBaank/simpleJson/actions/workflows/gradle.yml)

simpleJson is a simple json parser for the jvm made in kotlin.

### Usage

You can read using.

```kotlin
val json = JsonReader.tryRead(""" { a : "a", b : [1 , "1"] } """) //will return null if data is not a valid json
val json = JsonReader.read(""" { a : "a", b : [1 , "1"] } """) //will throw an exception if data is not a valid json
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

After reading, you can access the data using the `tryGet` methods which will return null if the key is not found or the
value is not of the correct type.

```kotlin
val json = JsonReader.read(data)
val name = json.tryGetString("name")
val age = json.tryGetInt("age")
val infoName = json.tryGetObject("info")?.tryGetString("name") ?: "unknown"
val isPublic =
    json.tryGetArray("photos")?.getOrNull(0)?.tryGetBoolean("isPublic") ?: throw Exception("isPublic not found")
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
    implementation("com.github.xBaank:simpleJson:1.0.1")
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
    <version>1.0.1</version>
</dependency>
```



