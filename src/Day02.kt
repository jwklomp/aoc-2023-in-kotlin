import java.util.Collections.max

data class Set(val g: Int = 0, val r: Int = 0, val b: Int = 0)
data class Game(val id: Int = 0, val sets: List<Set> = mutableListOf())

fun findVal(nrWithColors: List<List<String>>, colorToFind: String): Int {
    val foundElement = nrWithColors.firstOrNull { it.getOrNull(1) == colorToFind }
    return (foundElement?.get(0) ?: "0").toInt()
}

fun makeGame(input: String): Game {
    val gameId: Int = input.split(":")[0].replace("Game ", "").toInt()
    val setStrings: List<String> = input.split(":")[1].trim().split(";").map { it.trim() }
    val sets = setStrings.map { setString ->
        val nrWithColors = setString.split(",").map { it.trim().split(" ") }
        Set(r = findVal(nrWithColors, "red"), g = findVal(nrWithColors, "green"), b = findVal(nrWithColors, "blue"))
    }
    return Game(id = gameId, sets = sets)
}

fun calculatePower(game: Game): Int =
    max(game.sets.map { it.b }) * max(game.sets.map { it.g }) * max(game.sets.map { it.r })

fun main() {
    fun part1(input: List<String>): Int {
        val games = input.map { makeGame(it) }
        return games.filter { game -> game.sets.all { set -> set.r <= 12 && set.g <= 13 && set.b <= 14 } }
            .sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        val games = input.map { makeGame(it) }
        return games.sumOf { calculatePower(it) }
    }

    val testInput = readInput("Day02_test")
    // println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day02")
    // println(part1(input))
    println(part2(input))
}
