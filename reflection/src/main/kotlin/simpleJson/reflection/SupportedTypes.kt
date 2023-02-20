package simpleJson.reflection

import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType

internal val simpleSupportedTypes = setOf(
    String::class.createType(nullable = true),
    Int::class.createType(nullable = true),
    Double::class.createType(nullable = true),
    Float::class.createType(nullable = true),
    Long::class.createType(nullable = true),
    Short::class.createType(nullable = true),
    Byte::class.createType(nullable = true),
    Boolean::class.createType(nullable = true),
    Nothing::class.createType(nullable = true)
)
internal val arraySupportedTypes = setOf(
    List::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true),
    ArrayList::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true),
)
internal val supportedTypes = simpleSupportedTypes + arraySupportedTypes