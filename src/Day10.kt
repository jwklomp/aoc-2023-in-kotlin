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


fun <T> Grid2D<T>.findEnclosedCells(
    startCell: Cell<T>,
    cache: MutableMap<Pair<Int, Int>, List<Cell<T>>> = mutableMapOf()
): List<Cell<T>> {

    val cachedResult = cache[Pair(startCell.x, startCell.y)]
    if (cachedResult != null) {
        return cachedResult
    }

    val visited = mutableSetOf<Cell<T>>()
    val result = mutableListOf<Cell<T>>()

    fun dfs(cell: Cell<T>) {
        if (cell !in visited) {
            visited.add(cell)
            result.add(cell)
            val neighbors = getSurrounding(cell.x, cell.y).filter { it.value == "." }
            neighbors.forEach { neighbor ->
                dfs(neighbor)
            }
        }
    }

    dfs(startCell)

    // Check if any of the enclosed cells are on the grid edge
    val enclosedOnEdge = result.any { isOnEdge(it.x, it.y) }

    val finalResult = if (enclosedOnEdge) emptyList() else result
    cache[Pair(startCell.x, startCell.y)] = finalResult
    return finalResult
}

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
    val gridData = listOf(
        listOf(".", ".", ".", ".", ".", ".", ".", ".", ".", "."),
        listOf(".", ".", ".", ".", ".", ".", ".", ".", ".", "."),
        listOf(".", ".", ".", ".", "X", "X", "X", "X", ".", "."),
        listOf(".", ".", ".", ".", "X", ".", ".", "X", ".", "."),
        listOf(".", ".", ".", ".", "X", ".", ".", "X", ".", "."),
        listOf("X", "X", "X", "X", "X", "X", "X", "X", "X", "X"),
        listOf(".", ".", ".", ".", "X", ".", "X", ".", ".", "."),
        listOf(".", ".", ".", ".", "X", ".", "X", ".", ".", "."),
        listOf(".", ".", ".", ".", "X", ".", "X", ".", ".", "."),
        listOf(".", ".", ".", ".", "X", "X", "X", ".", ".", ".")
    )
//
//    val grid = Grid2D(gridData)
//    println(grid)
//    val nonground = grid.getAllCells().filterNot { it.value == "." }

    val transformedGrid = pipeGrid.clone { cell ->
        if(longestPath.contains(cell)) cell.value else "."
    }

    println(transformedGrid)

    transformedGrid.getAllCells().filterNot { longestPath.contains(it) }.forEach { it.value = "." }
    val cache = mutableMapOf<Pair<Int, Int>, List<Cell<String>>>()
    val result = longestPath.flatMap { ng ->
        val directGroundCells = transformedGrid.getSurrounding(ng.x, ng.y)
        val enclosedCells = directGroundCells.map { transformedGrid.findEnclosedCells(it, cache) }
        val cells = enclosedCells.flatten().distinct()
        println("Enclosed Cells for cell $ng is ${cells.size}: $cells")
        cells
    }

    return result.distinct().size
}

fun main() {
    val testInput = readInput("Day10_test")
    println(parts(testInput))
//    val input = readInput("Day10")
//    println(parts(input))

}

