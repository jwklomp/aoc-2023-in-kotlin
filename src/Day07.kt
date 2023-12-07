data class Hand(val cards: List<String>, val bidAmount: Long, var type: Int = 1, var cardSortNr: Long = 0)

// val cardOrder: List<String> = listOf("A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2")
val cardOrder: List<String> = listOf("A", "K", "Q", "T", "9", "8", "7", "6", "5", "4", "3", "2", "J")
val cardPoints = cardOrder.reversed()

fun calculateRank(cards: List<String>): Long {
    val nrs = cards.map { c -> cardPoints.indexOf(c) }.map { it.toString() }.map { if (it.length == 1) "0$it" else it }
    return nrs.joinToString("").toLong()
}

fun main() {
    fun makeGame(input: List<String>): List<Hand> =
        input.map {
            val (cardsStr, bidStr) = it.split(" ")
            val cards = cardsStr.chunked(1)
            Hand(cards = cards, bidAmount = bidStr.toLong(), cardSortNr = calculateRank(cards))
        }

    fun calculateRankWithoutJoker(game: List<Hand>) =
        game.forEach { hand ->
            val groupedCards = hand.cards.groupBy { it }
            hand.type = when {
                groupedCards.values.any { it.size == 5 } -> 7
                groupedCards.values.any { it.size == 4 } -> 6
                groupedCards.values.any { it.size == 3 } && groupedCards.values.any { it.size == 2 } -> 5
                groupedCards.values.any { it.size == 3 } -> 4
                groupedCards.values.filter { it.size == 2 }.size == 2 -> 3
                groupedCards.values.filter { it.size == 2 }.size == 1 -> 2
                else -> 1
            }
        }

    fun calculateRankWithJoker(game: List<Hand>) {
        game.forEach { hand ->
            val groupedCards = hand.cards.groupBy { it }
            val nrOfJokers = groupedCards.getOrDefault("J", emptyList()).size
            val groupedWithoutJoker = groupedCards.values.filterNot { it.contains("J") }
            val maxOfAKind = if (nrOfJokers < 5) groupedWithoutJoker.maxOf { it.size } else 0

            hand.type = when {
                maxOfAKind + nrOfJokers == 5 -> 7
                maxOfAKind + nrOfJokers == 4 -> 6
                groupedWithoutJoker.any { it.size == 3 } && groupedWithoutJoker.any { it.size == 2 } || groupedWithoutJoker.filter { it.size == 2 }.size == 2 && nrOfJokers == 1 -> 5
                groupedWithoutJoker.any { it.size == 3 } && nrOfJokers == 0 || groupedWithoutJoker.any { it.size == 2 } && nrOfJokers == 1 || nrOfJokers == 2 -> 4
                groupedWithoutJoker.filter { it.size == 2 }.size == 2 && nrOfJokers == 0 || groupedWithoutJoker.any { it.size == 2 } && nrOfJokers == 1 -> 3
                groupedWithoutJoker.filter { it.size == 2 }.size == 1 && nrOfJokers == 0 || nrOfJokers == 1 -> 2
                else -> 1
            }
        }
    }

    fun sortGame(game: List<Hand>): List<Hand> =
        game.sortedWith(
            compareBy(
                { it.type },
                { it.cardSortNr },
            ),
        )

    fun part1(input: List<String>): Long {
        val game = makeGame(input)
        calculateRankWithoutJoker(game)
        println(game)
        val sortedGame = sortGame(game)
        return sortedGame.mapIndexed { idx, hand -> (idx + 1) * hand.bidAmount }.sum()
    }

    fun part2(input: List<String>): Long {
        val game = makeGame(input)
        calculateRankWithJoker(game)
        val sortedGame = sortGame(game)
        return sortedGame.mapIndexed { idx, hand -> (idx + 1) * hand.bidAmount }.sum()
    }

    val testInput = readInput("Day07_test")
    // println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day07")
    // println(part1(input))
    println(part2(input))
}
