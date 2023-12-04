import kotlin.math.pow

data class Card(val id: Int, var amount: Int, val winningNumbers: List<Int>, val playNumbers: List<Int>)

fun makeCard(cardString: String): Card {
    val (nrString, winningString, playString) = cardString.replace("Card ", "").split(":", "|")
    return Card(
        id = nrString.trim().toInt(),
        amount = 1,
        winningNumbers = winningString.trim().split(" ").filter { it !== "" }.map { it.toInt() },
        playNumbers = playString.trim().split(" ").filter { it !== "" }.map { it.toInt() },
    )
}

fun main() {
    fun part1(input: List<String>): Int {
        val cards = input.map { makeCard(it) }
        val matchesPerCard = cards.map { card -> card.playNumbers.filter { playNumber -> card.winningNumbers.contains(playNumber) }.size }
        return matchesPerCard.sumOf { 2.0.pow(it - 1).toInt() }
    }

    fun part2(input: List<String>): Int {
        val cards = input.map { makeCard(it) }
        cards.forEach { card ->
            val matches = card.playNumbers.filter { playNumber -> card.winningNumbers.contains(playNumber) }.size
            val idsToUpdate = card.id + 1..card.id + matches
            cards.filter { idsToUpdate.contains(it.id) }.forEach { it.amount += card.amount }
        }
        return cards.sumOf { it.amount }
    }

    val testInput = readInput("Day04_test")
    // println(part1(testInput))
    // println(part2(testInput))

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
