import arrow.core.Either
import arrow.core.getOrElse

fun <B, A : Throwable> Either<A, B>.getOrThrow(): B = getOrElse { throw it }

@Suppress("USELESS_IS_CHECK")
inline fun <reified B, A : Throwable> Either<A, B>.isRightOrThrow(): Boolean = getOrElse { throw it } is B
