import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.max

// find the GCD (Greatest Common Divisor)
fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

// find the LCM (Least Common Multiple)
fun lcm(a: Long, b: Long): Long = abs(a * b) / gcd(a, b)

// Find the LCM of a list of numbers
fun findLCM(numbers: List<Long>): Long = numbers.reduce { acc, num -> lcm(acc, num) }

/**
 * Extension function to get all index positions of a given element in a collection
 */
fun <E> Iterable<E>.indexesOf(e: E) = mapIndexedNotNull { index, elem -> index.takeIf { elem == e } }

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun findIndex(haystack2D: List<List<String>>, needle: String): MutableList<Int> =
    mutableListOf(-1, -1).apply {
        haystack2D.forEachIndexed { i, r ->
            r.forEachIndexed { j, c ->
                if (c == needle) {
                    this[0] = j
                    this[1] = i
                }
            }
        }
    }

/**
 * Extension function that is like takeWhile, yet also takes the first element not making the test.
 */
fun <T> Iterable<T>.takeWhileInclusive(
    predicate: (T) -> Boolean,
): List<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}

data class Point(val x: Long, val y: Long)

fun manhattanDistance(first: Point, second: Point) = abs(first.x - second.x) + abs(first.y - second.y)

data class Interval(val from: Long, val to: Long)

fun mergeIntervals(intervals: List<Interval>) = intervals
    .sortedWith(compareBy { it.from })
    .fold(listOf<Interval>()) { sum, item ->
        val last = sum.lastOrNull()
        if (last != null && last.to >= item.from) {
            val old = sum.dropLast(1)
            old + Interval(from = last.from, to = max(last.to, item.to))
        } else {
            sum + item
        }
    }

inline fun <reified T> transpose(xs: List<List<T>>): List<List<T>> {
    val cols = xs[0].size
    val rows = xs.size
    return List(cols) { j ->
        List(rows) { i ->
            xs[i][j]
        }
    }
}

/**
 * Pick's Theorem: A = I + (B/2) - 1
 * Calculates the number of lattice points strictly inside a lattice polygon
 * See https://en.wikipedia.org/wiki/Pick%27s_theorem
 * @param area The area of the lattice polygon.
 * @param boundaryVertices The number of lattice points on the boundary of the polygon.
 * @return The number of lattice points strictly inside the polygon.
 */
fun calculateInsideVertices(area: Double, boundaryVertices: Double): Double {
    val insidePoints = area - (boundaryVertices / 2) + 1
    return if (insidePoints >= 0) insidePoints else 0.0
}

fun splitOnEmptyLine(input: List<String>): List<List<String>> =
    input.fold(mutableListOf(mutableListOf<String>())) { acc, string ->
        if (string.isBlank())
            acc.add(mutableListOf())
        else
            acc.last().add(string)
        acc
    }
