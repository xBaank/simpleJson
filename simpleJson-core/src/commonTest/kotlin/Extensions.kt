import arrow.core.Either
import arrow.core.getOrElse
import kotlin.test.assertTrue

fun <B, A : Throwable> Either<A, B>.getOrThrow(): B = getOrElse { throw it }

@Suppress("USELESS_IS_CHECK")
inline fun <reified B, A : Throwable> Either<A, B>.isRightOrThrow(): Boolean = getOrElse { throw it } is B

fun assert(isRight: Boolean) = assertTrue { isRight }
