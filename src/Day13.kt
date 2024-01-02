import kotlin.math.floor
import kotlin.math.max

data class IndexedString(val index: Int, val str: String)

fun getMirroredNumber(
    input: List<String>,
    isReversed: Boolean = false,
): Int {
    tailrec fun determine(
        org: List<String>,
        rev: List<String>,
    ): Int {
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

fun findStringsOneCharDifferent(list: List<String>): List<Pair<IndexedString, IndexedString>> {
    val result = mutableListOf<Pair<IndexedString, IndexedString>>()
    for (i in list.indices) {
        for (j in i + 1 until list.size) {
            val el1 = list[i]
            val el2 = list[j]

            if (areStringsOneCharDifferent(el1, el2)) {
                result.add(Pair(IndexedString(index = i, str = el1), IndexedString(index = j, str = el2)))
            }
        }
    }
    return result
}

fun areStringsOneCharDifferent(
    str1: String,
    str2: String,
): Boolean = str1.length == str2.length && str1.zip(str2).count { (char1, char2) -> char1 != char2 } == 1

fun findAndRepairSmudge(pattern: List<String>): Int {
    // for all candidates, check if a new reflection line occurs (so also determine the existing)
    val originalReflection = analyzeAndReturnNumber(pattern)

    val rowPairs = findStringsOneCharDifferent(pattern)
    rowPairs.forEach { println("horizontal candidate smudge: $it") }
    // for each pair we can replace second with first or first with second.
    val rowReplacements =
        rowPairs.map { pair ->
            val replacedSecond = pattern.mapIndexed { index, s -> if (index == pair.second.index) pair.first.str else s }
            val newReflectionRS = analyzeAndReturnNumber(replacedSecond)

            val replacedFirst = pattern.mapIndexed { index, s -> if (index == pair.first.index) pair.second.str else s }
            val newReflectionRF = analyzeAndReturnNumber(replacedFirst)

            if (newReflectionRS != 0 && newReflectionRS != originalReflection && newReflectionRF != 0 && newReflectionRF != newReflectionRS) {
                println(
                    "warning multiple values",
                )
            }
            newReflectionRS
        }

    val transposed =
        transpose(pattern.map { it.chunked(1) })
            .map { it.joinToString("") }
    val colPairs = findStringsOneCharDifferent(transposed)

    colPairs.forEach { println("vertical candidate smudge: $it") }
    val colReplacements =
        colPairs.map { pair ->
            val replacedSecond = transposed.mapIndexed { index, s -> if (index == pair.second.index) pair.first.str else s }

            val newReflectionRS =
                analyzeAndReturnNumber(
                    transpose(replacedSecond.map { it.chunked(1) })
                        .map { it.joinToString("") },
                )

            val replacedFirst = transposed.mapIndexed { index, s -> if (index == pair.first.index) pair.second.str else s }
            val newReflectionRF =
                analyzeAndReturnNumber(
                    transpose(replacedFirst.map { it.chunked(1) })
                        .map { it.joinToString("") },
                )

            if (newReflectionRS != 0 && newReflectionRS != originalReflection && newReflectionRF != 0 && newReflectionRF != newReflectionRS) {
                println(
                    "error, multiple values",
                )
            }
            newReflectionRS
        }
    val totalReplacements = (rowReplacements + colReplacements).filter { it > 0 && it != originalReflection }.distinct()
    println("found following replacement(s) $totalReplacements")
    if (totalReplacements.size != 1) {
        println("error ${totalReplacements.size} replacements")
        pattern.forEach { println(it) }
    }
    if (totalReplacements.first() == originalReflection) {
        println("error no change in reflection $originalReflection for ")
        pattern.forEach { println(it) }
    }

    return totalReplacements.first()
}

fun main() {
    fun part1(input: List<String>): Int {
        val patternStrings = splitOnEmptyLine(input)
        return patternStrings.sumOf { analyzeAndReturnNumber(it) }
    }

    /**
     * Smudge has 2 characteristics
     * a) there are 2 horizontal or vertical lines that are exactly ONE character different
     * b) if this one character is changed, this results in a DIFFERENT reflection line. There are 2 options: change line P to match line Q, or change line Q to match line P.
     * Note that there could be multiple elements satisfying a, but only one should satisfy a and b
     */
    fun part2(input: List<String>): Int {
        val patternStrings = splitOnEmptyLine(input)
        return patternStrings.sumOf { findAndRepairSmudge(it) }
    }

    val testInput = readInput("Day13_test")
    // println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day13")
    // println(part1(input))
    // println(part2(input))
}
