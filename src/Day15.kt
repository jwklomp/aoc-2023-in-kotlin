private fun asciiHash(inputString: String): Int =
    inputString.fold(0) { acc, char ->
        val asciiValue = char.code
        ((acc + asciiValue) * 17) % 256
    }

fun main() {
    fun part1(input: List<String>): Int {
        val strings = input.map { it.split(",") }.first()
        return strings.sumOf { asciiHash(it) }
    }

    fun part2(input: List<String>): Int {
        val result = input.map { it.split(",") }
        println(result)
        return 1
    }

    val testInput = readInput("Day15_test")
    println(part1(testInput))
    // println(part2(testInput))

    val input = readInput("Day15")
    println(part1(input))
    // println(part2(input))
}
