import kotlin.math.min

fun findAllMirrorAxes(pattern: List<String>): List<Int> {
    val rowLength = pattern.first().length
    val mirrorAxes = mutableListOf<Int>()
    // find rows
    for (i in 1 until pattern.size) {
        val firstHalf = pattern.subList(0, i)
        val secondHalf = pattern.subList(i, min(2 * i, pattern.size))
        if (firstHalf.takeLast(secondHalf.size).reversed() == secondHalf) {
            mirrorAxes.add(i * 100)
        }
    }
    // find columns
    for (j in 1 until rowLength) {
        val firstHalf = pattern.map { it.substring(0, j) }
        val secondHalf = pattern.map { it.substring(j, min(2 * j, rowLength)) }
        val firstHalfReversed = firstHalf.map { it.takeLast(secondHalf[0].length).reversed() }
        if (firstHalfReversed == secondHalf) {
            mirrorAxes.add(j)
        }
    }

    return mirrorAxes
}

fun findAndRepairSmudge(pattern: List<String>): Int {
    val original = findAllMirrorAxes(pattern).first()
    val results = mutableListOf<Int>()
    // brute force approach is performant enough, iterate though all characters in pattern and flip them one by one.
    for ((index, line) in pattern.withIndex()) {
        for (charIndex in line.indices) {
            val modifiedPattern = pattern.toMutableList()
            modifiedPattern[index] =
                line.substring(0, charIndex) + (if (line[charIndex] == '.') '#' else '.') + line.substring(charIndex + 1)

            val result = findAllMirrorAxes(modifiedPattern).firstOrNull { it != original }
            if (result != null) results.add(result)
        }
    }

    return results.distinct().first()
}

fun main() {
    fun part1(input: List<String>): Int {
        val patternStrings = splitOnEmptyLine(input)
        return patternStrings.sumOf { findAllMirrorAxes(it).first() }
    }

    fun part2(input: List<String>): Int {
        val patternStrings = splitOnEmptyLine(input)
        return patternStrings.sumOf { findAndRepairSmudge(it) }
    }

    val testInput = readInput("Day13_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
