import arrow.core.Either
import arrow.core.getOrHandle

fun <B, A : Throwable> Either<A, B>.getOrThrow(): B = getOrHandle { throw it }
@Suppress("USELESS_IS_CHECK")
inline fun <reified B, A : Throwable> Either<A, B>.isRightOrThrow(): Boolean = getOrHandle { throw it } is B
