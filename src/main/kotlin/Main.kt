fun main(args: Array<String>) {
    val data = JsonReader.read<String>("   { \"name\":  \"John\", \"age\": 30, \"car\": null }")
}