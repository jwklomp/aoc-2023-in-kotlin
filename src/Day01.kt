fun main() {
    fun part1(input: List<String>): Int =
        input
            .asSequence()
            .map { it.split("") }
            .map { it.filter { it != "" } }
            .map { it.filter { it.toDoubleOrNull() != null } }
            .mapNotNull { (it.first() + it.last()).toIntOrNull() }
            .sum()

    fun replaceNumbers(input: String): String {
        val pattern = Regex("(oneight|threeight|nineight|fiveight|eightwo|eighthree|twone|sevenine|one|two|three|four|five|six|seven|eight|nine)")
        val result = StringBuilder(input)
        var offset = 0

        pattern.findAll(input).forEach { matchResult ->
            val replacement = when (matchResult.value) {
                // Nasty elvses! Wicked, tricksy, false!
                "oneight" -> "18"
                "threeight" -> "38"
                "fiveight" -> "58"
                "nineight" -> "98"
                "eightwo" -> "82"
                "eighthree" -> "83"
                "twone" -> "21"
                "sevenine" -> "79"
                "one" -> "1"
                "two" -> "2"
                "three" -> "3"
                "four" -> "4"
                "five" -> "5"
                "six" -> "6"
                "seven" -> "7"
                "eight" -> "8"
                "nine" -> "9"
                else -> ""
            }

            val start = matchResult.range.first + offset
            val end = matchResult.range.last + 1 + offset

            result.replace(start, end, replacement)
            offset += replacement.length - matchResult.value.length
        }

        return result.toString()
    }

    fun part2(input: List<String>): Int =
        input
            .asSequence()
            .map { replaceNumbers(it) }
            .map { it.split("") }
            .map { it.filter { it != "" } }
            .map { it.filter { it.toDoubleOrNull() != null } }
            .sumOf { (it.first() + it.last()).toInt() }

    val testInput = readInput("Day01_test")
    // println(part1(testInput))
    // println(test(listOf("two1nine")))
    println(part2(testInput))

    val input = readInput("Day01")
    // println(part1(input))
    println(part2(input))
}
