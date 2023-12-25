import kotlin.math.abs
import kotlin.math.floor

fun makePipeGrid(input: List<String>): Grid2D<String> {
    val chunked = input.filterNot { it.isEmpty() }.map { it.chunked(1) }
    return Grid2D(chunked)
}

tailrec fun getPath(
    c: Cell<String>,
    previous: Cell<String>?,
    grid: Grid2D<String>,
    path: List<Cell<String>>
): List<Cell<String>> {
    val connectedCells =
        if (previous == null && c.value == "S") getConnectedToStart(grid, c) else getConnectedCells(grid, c)
    val nextCells = connectedCells.filterNot { it == previous } // remove previous, as we should not go back.
    if (nextCells.isEmpty()) println("error no next cells for $c, previous $previous")
    return if (previous !== null && c.value == "S") {
        path
    } else {
        println(c)
        getPath(nextCells[0], c, grid, path + c)
    }
}

private fun getConnectedToStart(grid: Grid2D<String>, c: Cell<String>): List<Cell<String>> {
    val surroundingCells = grid.getSurrounding(c.x, c.y).filterNot { it.value == "." }
    val connectedToS = surroundingCells.filter { sc ->
        when (sc.value) {
            "|" -> abs(sc.y - c.y) == 1 && sc.x == c.x
            "-" -> abs(sc.x - c.x) == 1 && sc.y == c.y
            "L" -> c.x == sc.x + 1 && c.y == sc.y || c.y == sc.y - 1 && c.x == sc.x
            "J" -> c.x == sc.x - 1 && c.y == sc.y || c.y == sc.y - 1 && c.x == sc.x
            "7" -> c.x == sc.x - 1 && c.y == sc.y || c.y == sc.y + 1 && c.x == sc.x
            "F" -> c.x == sc.x + 1 && c.y == sc.y || c.y == sc.y + 1 && c.x == sc.x
            else -> false
        }
    }
    return connectedToS
}

fun getConnectedCells(grid: Grid2D<String>, c: Cell<String>): List<Cell<String>> {
    val surroundingCells = grid.getSurrounding(c.x, c.y).filterNot { it.value == "." }
    val connectedCells = when (c.value) {
        "|" -> surroundingCells.filter { sc -> abs(sc.y - c.y) == 1 && sc.x == c.x }
        "-" -> surroundingCells.filter { sc -> abs(sc.x - c.x) == 1 && sc.y == c.y }
        "L" -> surroundingCells.filter { sc -> sc.x == c.x + 1 && sc.y == c.y || sc.y == c.y - 1 && sc.x == c.x }
        "J" -> surroundingCells.filter { sc -> sc.x == c.x - 1 && sc.y == c.y || sc.y == c.y - 1 && sc.x == c.x }
        "7" -> surroundingCells.filter { sc -> sc.x == c.x - 1 && sc.y == c.y || sc.y == c.y + 1 && sc.x == c.x }
        "F" -> surroundingCells.filter { sc -> sc.x == c.x + 1 && sc.y == c.y || sc.y == c.y + 1 && sc.x == c.x }
        "S" -> surroundingCells // actually unknown, but for the time being set all surrounding cells.
        else -> emptyList()
    }
    return connectedCells
}

fun main() {
    // note that the structure is a loop, each pipe is connected to 2 other pipes.
    // So no need to use a graph and use DFS etc., vast amounts of time were wasted...
    fun parts(input: List<String>): Int {
        val pipeGrid = makePipeGrid(input)
        val allCells = pipeGrid.getAllCells()
        val start = allCells.first { it.value == "S" }

        val longestPath = getPath(start, null, pipeGrid, emptyList())  // includes start, excludes end
        val printGrid = allCells.map { cell -> longestPath.indexOf(cell) }
            .chunked(pipeGrid.getNrOfColumns())
        printGrid.forEach { row -> println(row.joinToString("  ").replace("-1", ".")) }
        println(floor(((longestPath.size) / 2).toDouble()).toInt()) // floor because including start, if excluding start should use ceil

        // part 2
        val totalEnclosedList = mutableListOf<Cell<String>>()
        val totalUnenclosedList = mutableListOf<Cell<String>>()

        data class ProcessedCellResult(
            val enclosedList: List<Cell<String>> = mutableListOf(),
            val unenclosedList: List<Cell<String>> = mutableListOf()
        )

        fun processSurroundingCells(cell: Cell<String>, processedCells: Set<Cell<String>>): ProcessedCellResult {
            val resultEnclosed = mutableSetOf<Cell<String>>()
            val resultUnenclosed = mutableSetOf<Cell<String>>()
            val stack = mutableListOf<Pair<Cell<String>, Set<Cell<String>>>>()

            stack.add(Pair(cell, processedCells))

            while (stack.isNotEmpty()) {
                val (currentCell, currentProcessedCells) = stack.removeAt(0)

                if (pipeGrid.isOnEdge(currentCell.x, currentCell.y)) {
                    println("adding currentProcessedCells $currentProcessedCells to unenclosed")
                    resultUnenclosed.addAll(currentProcessedCells)
                    break
                } else {
                    val newCells = pipeGrid.getAdjacent(currentCell.x, currentCell.y)
                        .filter { it.value == "." }
                        .filterNot { currentProcessedCells.contains(it) }

                    if (newCells.isEmpty()) {
                        println("adding currentProcessedCells $currentProcessedCells to enclosed")
                        resultEnclosed.addAll(currentProcessedCells)
                        break
                    } else {
                        for (adjacentCell in newCells) {
                            stack.add(Pair(adjacentCell, currentProcessedCells + currentCell))
                        }
                    }
                }
            }

            return ProcessedCellResult(resultEnclosed.toList(), resultUnenclosed.toList())
        }

        fun processCell(groundNextToPipeCell: Cell<String>) {
            println("processing groundNextToPipeCell $groundNextToPipeCell")
            val (enclosedList, unenclosedList) = processSurroundingCells(groundNextToPipeCell, mutableSetOf())
            totalEnclosedList.addAll(enclosedList)
            totalUnenclosedList.addAll(unenclosedList)
        }

        longestPath.forEach { pipeCell ->
            val groundNextToPipeCells = pipeGrid.getAdjacent(pipeCell.x, pipeCell.y).filter { it.value == "." }
            groundNextToPipeCells.forEach { gc ->
                // skip if already processed
                if (!totalEnclosedList.contains(gc) && !totalUnenclosedList.contains(gc)) processCell(gc)
            }
        }

        return totalEnclosedList.distinct().size
    }

    val testInput = readInput("Day10_test")
    println(parts(testInput))

//    val input = readInput("Day10")
//    println(parts(input))
}
