data class SpaceElement(var starId: Int = 0, var xX: Long = 0, var xY: Long = 0, val content: String)

fun makeSpaceGrid(input: List<String>): Grid2D<SpaceElement> {
    val chunked = input.map { it.chunked(1).map { c -> SpaceElement(content = c) } }
    return Grid2D(chunked)
}
fun makePairs(elements: List<SpaceElement>): List<Pair<SpaceElement, SpaceElement>> =
    elements.flatMapIndexed { i, first ->
        elements.drop(i + 1).map { second ->
            Pair(first, second)
        }
    }

fun main() {
    fun part1(input: List<String>): Long {
        val spaceGrid = makeSpaceGrid(input)
        val starCells = spaceGrid.getAllCells().filterNot { it.value.content == "." }

        starCells.forEachIndexed { i, cell ->
            cell.value.xX = cell.x.toLong()
            cell.value.xY = cell.y.toLong()
            cell.value.starId = i + 1
        }

        val emptyColNrs = (0..<spaceGrid.getNrOfColumns()).mapNotNull { colNr ->
            if (spaceGrid.getCol(colNr).all { it.value.content == "." }) colNr else null
        }

        val emptyRowNrs = (0..<spaceGrid.getNrOfRows()).mapNotNull { rowNr ->
            if (spaceGrid.getRow(rowNr).all { it.value.content == "." }) rowNr else null
        }

        // expand the universe
        starCells.forEach { starCell ->
            starCell.value.xX += (emptyColNrs.filter { it < starCell.x }.size) * (1000000 - 1) // mind the -1
            starCell.value.xY += (emptyRowNrs.filter { it < starCell.y }.size) * (1000000 - 1)
        }

        val starPairs = makePairs(starCells.map { it.value })

        // get Manhattan distance and sum it
        return starPairs.sumOf { starPair ->
            val first = starPair.first
            val second = starPair.second
            manhattanDistance(Point(first.xX, first.xY), Point(second.xX, second.xY))
        }
    }

    val testInput = readInput("Day11_test")
    println(part1(testInput))

    val input = readInput("Day11")
    println(part1(input))
}
