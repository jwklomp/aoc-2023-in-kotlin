import kotlin.math.floor
import kotlin.math.max

fun getMirroredNumber(input: List<String>, isReversed: Boolean = false): Int {
    println(isReversed)
    input.forEach { println(it) }
    fun determine(original: List<String>, reversed: List<String>): Int {
        return if (original.size <= 1) {
            0 // with 1 row left there is no mirror, so return 0
        } else if (original == reversed) {
            if (isReversed) {
                (input.size - original.size) + floor(original.size / 2.0).toInt()
            } else {
                floor(original.size / 2.0).toInt()
            }
        } else {
            determine(original.dropLast(1), reversed.drop(1))
        }
    }
    return determine(input, input.reversed())
}

fun analyzeAndReturnNumber(input: List<String>): Int {
    val horizontalLine = max(getMirroredNumber(makeEven(input)), getMirroredNumber(reverseAndMakeEven(input), true))
    println(horizontalLine)

    val transposedInput = transpose(input.map { it.chunked(1) })
        .map { it.joinToString("") }
    val verticalLine = max(getMirroredNumber(makeEven(transposedInput)), getMirroredNumber(reverseAndMakeEven(transposedInput), true))
    println(verticalLine)

    return (horizontalLine * 100) + verticalLine
}

private fun makeEven(input: List<String>): List<String> =
    if (input.size % 2 == 1) input.dropLast(1) else input

private fun reverseAndMakeEven(input: List<String>): List<String> =
    if (input.size % 2 == 1) input.reversed().dropLast(1) else input.reversed()

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
    //println(part1(input))
    //println(part2(input))
}
