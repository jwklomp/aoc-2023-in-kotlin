

fun main() {
    fun part1(input: List<String>): Int =
        input
            .map { it.split("") }
            .map { it.filter { it != "" } }
            .map { it.filter { it.toDoubleOrNull() != null } }
            .map { (it.first() + it.last()).toIntOrNull() }
            .filterNotNull()
            .sum()

    fun replaceNumbers(input: String): String {
        val pattern = Regex("(one|two|three|four|five|six|seven|eight|nine)")
        val result = StringBuilder(input)
        var offset = 0

        pattern.findAll(input).forEach { matchResult ->
            val replacement = when (matchResult.value) {
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

    fun test(input: List<String>): List<String> =
        input
            .map { replaceNumbers(it) }

//    fun numberListToNumber(input: List<String>): Int? =
//        if (input.isEmpty()) {
//            println("no numbers found")
//            null
//        } else if (input.size == 1) {
//            println("only 1 found")
//            //input.first().toInt()
//            //(input.first() + "0").toInt()
//            (input.first() + input.last()).toInt()
//        } else {
//            (input.first() + input.last()).toInt()
//        }

    fun part2(input: List<String>): Int =
        input
            .map { replaceNumbers(it) }
            .map { it.split("") }
            .map { it.filter { it != "" } }
            .map { it.filter { it.toDoubleOrNull() != null } }
            .mapNotNull { (input.first() + input.last()).toInt() }
            .sum()

    val testInput = readInput("Day01_test")
    // println(part1(testInput))
    // println(test(testInput))
    // println(test(listOf("two1nine")))
    println(part2(testInput))

    val input = readInput("Day01")
    // println(part1(input))
    println(test(input))
    println(part2(input))
}
