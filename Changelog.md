## v9.0.0
- Add set operators for JsonNode, now you can do ```json["key"] = "value"``` to set or create a value
- Rename methods from to.. to as.. (asInt, asString, asBoolean, asDouble, asLong, asFloat...)
- Add serialization in reflection module
- Fix type erasure in reflection module when using generic types

## v8.0.1
- inline functions with functional parameters

## v8.0.0
- Add unaryPlus for jArray ```jArray { +"hola" }```
- Add module to use reflection to deserialize objects into data classes with simple types ```Number, String, Boolean, List, Other Data class```
- Changed to multi module project
## v7.1.0

- Add String.UnaryPlus operator ```"number" += 5```
- Deprecated jArray and jObject in jObjectBuilder in favor of ```"object" += jObject { }```

## v7.0.0

- Changed nullable types with Either type

## v6.0.0

- Changed constructor of PrettyPrintWriter to use composition
- Added extension method to JsonWriter to use PrettyPrintWriter
  ```JsonWriter(stream).prettyPrint()```
- Properly escape control characters when writing ````JsonString````
