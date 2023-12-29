import kotlin.math.floor
import kotlin.math.max

fun getMirroredNumber(input: List<String>, isReversed: Boolean = false): Int {
    tailrec fun determine(org: List<String>, rev: List<String>): Int {
        return if (org.size <= 1) {
            0 // with 1 row left there is no mirror, so return 0
        } else if ((org.size % 2 == 0) && org == rev) {
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

fun main() {
    fun part1(input: List<String>): Int {
        val patternStrings = splitOnEmptyLine(input)
        return patternStrings.sumOf { analyzeAndReturnNumber(it) }
    }

    fun part2(input: List<String>): Int {
        val result = input.map { it.split(",") }
        println(result)
        return 1
    }

    val testInput = readInput("Day13_test")
    println(part1(testInput))
    //println(part2(testInput))

    val input = readInput("Day13")
    println(part1(input))
    //println(part2(input))
}
