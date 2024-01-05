data class Lens(val code: String, val focalLength: Int)

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
        val strings = input.map { it.split(",") }.first()
        val boxes = MutableList<MutableList<Lens>>(256) { mutableListOf() }
        strings.forEach { seq ->
            val operation = if (seq.endsWith("-")) "-" else "="
            val code = seq.substringBefore(operation)
            val focalLength = if (operation == "=") seq.substringAfter("=").toInt() else 0
            val boxIndex = asciiHash(code)
            val currentLenses = boxes[boxIndex]
            if (operation == "-") {
                boxes[boxIndex] = currentLenses.filterNot { it.code == code }.toMutableList()
            } else {
                val newLens = Lens(code = code, focalLength = focalLength)
                val lensIndex = currentLenses.indexOfFirst { it.code == newLens.code }
                if (lensIndex != -1) currentLenses[lensIndex] = newLens else currentLenses.add(newLens)
            }
        }
        return boxes.mapIndexed { boxIndex, lenses ->
            (boxIndex + 1) * lenses.mapIndexed { lensIndex, lens -> (lensIndex + 1) * lens.focalLength }.sum()
        }.sum()
    }

    val testInput = readInput("Day15_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
