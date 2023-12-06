data class Race(val timeRange: LongRange, val distance: Long)

fun main() {
    fun part1(): Long {
        // val races = listOf(Race(1..7, 9), Race(1..15, 40), Race(1..30, 200))
        // val races = listOf(Race(1..71530, 940200))
        // val races = listOf(Race(1..62, 553), Race(1..64, 1010), Race(1..91, 1473), Race(1..90, 1074))
        val races = listOf(Race(1.toLong()..62649190.toLong(), 553101014731074)) // That is about 13.802 times around the earth...

        val wins = races.map { race ->
            val max = race.timeRange.last
            val distances = race.timeRange.map { pressedTime -> (pressedTime * (max - pressedTime)).toLong() }
            distances.filter { d -> d > race.distance }.size.toLong()
        }

        return wins.reduce { sum, current -> sum * current }
    }

    println(part1())
}
