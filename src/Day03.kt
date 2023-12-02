fun main() {
    fun part1(input: List<String>): Int {
        val result = input.map { it.split(",") }
        println(result)
        return 1
    }

    fun part2(input: List<String>): Int  {
        val result = input.map { it.split(",") }
        println(result)
        return 1
    }

    val testInput = readInput("Day03_test")
    println(part1(testInput))
    //println(part2(testInput))

    val input = readInput("Day03")
    //println(part1(input))
    //println(part2(input))
}
