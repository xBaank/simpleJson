## simpleJson

simpleJson is a simple json parser for the jvm made in kotlin.

### Usage

You can read using.

```kotlin
val json = JsonReader.tryRead(data) //will return null if data is not a valid json
val json = JsonReader.read(data) //will throw an exception if data is not a valid json
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

After reading, you can access the data using the `tryGet` methods which will return null if the key is not found or the value is not of the correct type.

```kotlin
val json = JsonReader.read(data)
val name = json.tryGetString("name")
val age = json.tryGetInt("age")
val infoName = json.tryGetObject("info")?.tryGetString("name") ?: "unknown"
val isPublic = json.tryGetArray("photos")?.getOrNull(0)?.tryGetBoolean("isPublic") ?: throw Exception("isPublic not found")
```

