import kotlin.math.floor
import kotlin.math.max

fun getMirroredNumber(input: List<String>, isReversed: Boolean = false): Int {
    tailrec fun determine(org: List<String>, rev: List<String>): Int {
        return if (org.size <= 1) {
            0 // with 1 row left there is no mirror, so return 0
        } else if ((org.size % 2 == 0) && org == rev) { // note: no mirror possible if uneven amount of rows.
            if (isReversed) (input.size - org.size) + floor(org.size / 2.0).toInt() else floor(org.size / 2.0).toInt()
        } else {
            determine(org.dropLast(1), rev.drop(1))
        }
    }
    return determine(input, input.reversed())
}

fun analyzeAndReturnNumber(input: List<String>): Int {
    val horizontalLine = max(getMirroredNumber(input), getMirroredNumber(input.reversed(), true))
    val transposedInput = transpose(input.map { it.chunked(1) }).map { it.joinToString("") }
    val verticalLine = max(getMirroredNumber(transposedInput), getMirroredNumber(transposedInput.reversed(), true))
    return (horizontalLine * 100) + verticalLine
}
fun findStringsOneCharDifferent(strings: List<String>): Pair<String, String>? {
    return strings.asSequence()
        .flatMapIndexed { index1, str1 ->
            strings.drop(index1 + 1).asSequence().map { str2 -> Pair(str1, str2) }
        }
        .firstOrNull { (str1, str2) -> areStringsOneCharDifferent(str1, str2) }
}

fun areStringsOneCharDifferent(str1: String, str2: String): Boolean =
    str1.length == str2.length && str1.zip(str2).count { (char1, char2) -> char1 != char2 } == 1

fun repairSmudges(input: List<String>): List<String> {
    val patternStrings = splitOnEmptyLine(input)
    val rows = patternStrings.mapNotNull { findStringsOneCharDifferent(it) }
    rows.forEach { println("horizontal smudge: $it") }

    val cols = patternStrings.mapNotNull {patternString ->
        val transposed = transpose(patternString.map { it.chunked(1) })
            .map{ it.joinToString("") }
        findStringsOneCharDifferent(transposed)
    }
    cols.forEach { println("vertical smudge: $it") }
    println("number of smudges for pattern ${rows.size + cols.size}")
    return input
}

fun main() {
    fun part1(input: List<String>): Int {
        val patternStrings = splitOnEmptyLine(input)
        return patternStrings.sumOf { analyzeAndReturnNumber(it) }
    }

    fun part2(input: List<String>): Int {
        val repairedInput = repairSmudges(input)
        return part1(repairedInput)
    }

    val testInput = readInput("Day13_test")
    //println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day13")
    //println(part1(input))
    //println(part2(input))
}
