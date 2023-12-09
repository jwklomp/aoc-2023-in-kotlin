fun extrapolateReading(values: List<Int>, extrapolatedValue: Int): Int {
    return if (values.all { it == 0 }) {
        extrapolatedValue
    } else {
        val derivativeValues = values.windowed(2).map { it.last() - it.first() }
        val lastPair = values.takeLast(2)
        extrapolateReading(derivativeValues, extrapolatedValue + lastPair.last() - lastPair.first())
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val readings = input.map { reading -> reading.split(" ").map { it.toInt() } }
        return readings.sumOf { extrapolateReading(it, it.last()) }
    }

    fun part2(input: List<String>): Int {
        val readings = input.map { reading -> reading.split(" ").map { it.toInt() }.reversed() }
        return readings.sumOf { extrapolateReading(it, it.last()) }
    }

    val testInput = readInput("Day09_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
