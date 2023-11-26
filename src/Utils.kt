import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.max

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

data class Point(val x: Int, val y: Int)

fun manhattanDistance(first: Point, second: Point) = abs(first.x - second.x) + abs(first.y - second.y)

data class Interval(val from: Int, val to: Int)

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
