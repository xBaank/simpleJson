## v6.0.0
- Changed constructor of PrettyPrintWriter to use composition
- Added extension method to JsonWriter to use PrettyPrintWriter 
```JsonWriter(stream).prettyPrint()```
- Properly escape control characters when writing ````JsonString````