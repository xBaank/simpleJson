## 4.0.0

- Updated dependencies
- Changed JVM target to 11

## 3.0.4

- Updated dependencies

## 3.0.3

- Updated dependencies

## 3.0.2

- Updated dependencies

## 3.0.1

- Updated dependencies
- Removed serialization module

## 3.0.0

- Improve performance
- Added nullable parameter to `JsonNode` methods

## v2.1.3

- Added nullable parameters in methods

## core v2.0.1

- Removed watchosX64 target

## reflection v2.0.3

- Updated dependencies

## core v2.0.0

- Updated kotlin to 1.8.20
- Changed JsonReader and JsonWriter visibility to internal
- New way to serialize is with extension methods
    - JsonNode.serialized()
    - JsonNode.serializedPretty(indent: String)
    - JsonNode.serializedTo(sink: BufferedSink)
    - JsonNode.serializedPrettyTo(sink: BufferedSink, indent: String)
- New way to deserialize is with extension methods
    - String.deserialized()
    - BufferedSource.deserialized()

## reflection v2.0.2

- Updated kotlin to 1.8.20
- Updated dependencies

## core v1.1.1

- More specific exceptions

## reflection v2.0.1

- Updated dependencies

## core v1.1.0

- Rewritten reader to be more specific
- Added more exceptions types

## reflection v2.0.0

- Added custom exceptions for module

## v1.0.2

- Added more targets

## v1.0.1

- Added support to decode exponential numbers

## v1.0.0

- Inital release

