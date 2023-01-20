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