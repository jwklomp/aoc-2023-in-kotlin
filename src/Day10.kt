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
    return if (previous !== null && c.value == "S") {
        path
    } else {
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

// note that the structure is a loop, each pipe is connected to 2 other pipes.
// So no need to use a graph and use DFS etc., vast amounts of time were wasted...
fun parts(input: List<String>): Double {
    val pipeGrid = makePipeGrid(input)
    val allCells = pipeGrid.getAllCells()
    val start = allCells.first { it.value == "S" }

    val longestPath = getPath(start, null, pipeGrid, emptyList())  // includes start, excludes end
    println(floor(((longestPath.size) / 2).toDouble()).toInt()) // floor because including start, if excluding start should use ceil

    // Use shoelace algorithm https://www.101computing.net/the-shoelace-algorithm/ to calculate the inside area of the polygon
    // and then use Pick's theorem https://en.wikipedia.org/wiki/Pick%27s_theorem to calculate the number of vertices inside the polygon
    val polygonArea = calculateShoelaceArea(longestPath)
    return calculateInsideVertices(area = polygonArea, boundaryVertices = longestPath.size.toDouble())
}

fun main() {
    // val testInput = readInput("Day10_test")
    // println(parts(testInput))
    val input = readInput("Day10")
    println(parts(input))
}

