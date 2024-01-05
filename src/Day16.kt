data class MirrorElement(val type: String, var visited: Boolean = false)

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}

private fun shineBeamFromAllDirections(mirrorGrid: Grid2D<MirrorElement>): Int {
    fun getCellDirections(cell: Cell<MirrorElement>): List<Pair<Cell<MirrorElement>, Direction>> =
        when (cell.x) {
            0 -> { // left edge
                when (cell.y) {
                    0 -> listOf(Pair(cell, Direction.RIGHT), Pair(cell, Direction.DOWN)) // top left
                    mirrorGrid.getNrOfRows() - 1 -> listOf(Pair(cell, Direction.RIGHT), Pair(cell, Direction.UP)) // bottom left
                    else -> listOf(Pair(cell, Direction.RIGHT))
                }
            }

            mirrorGrid.getNrOfColumns() - 1 -> { // right edge
                when (cell.y) {
                    0 -> listOf(Pair(cell, Direction.LEFT), Pair(cell, Direction.DOWN)) // top right
                    mirrorGrid.getNrOfRows() - 1 -> listOf(Pair(cell, Direction.LEFT), Pair(cell, Direction.UP)) // bottom right
                    else -> listOf(Pair(cell, Direction.LEFT))
                }
            }

            else -> { // not left or right edge
                when (cell.y) {
                    0 -> listOf(Pair(cell, Direction.DOWN)) // top
                    mirrorGrid.getNrOfRows() - 1 -> listOf(Pair(cell, Direction.UP)) // bottom
                    else -> listOf()
                }
            }
        }

    val allCellDirections =
        mirrorGrid.getAllCells()
            .filter { cell -> mirrorGrid.isOnEdge(cell.x, cell.y) }
            .flatMap { cell -> getCellDirections(cell) }

    return allCellDirections.maxOf { d -> shineBeam(mirrorGrid, d.first, d.second) }
}

private fun shineBeam(
    grid: Grid2D<MirrorElement>,
    startCell: Cell<MirrorElement>,
    startDirection: Direction,
): Int {
    grid.getAllCells().forEach { it.value.visited = false } // reset visited on each separate call of shineBeam
    val visitedList = mutableListOf<String>() // list of visited cells with direction

    fun step(
        cell: Cell<MirrorElement>,
        direction: Direction,
    ) {
        // return if cell is visited with the same direction, to prevent infinite loops
        val key = "${cell.x}-${cell.y}-$direction"
        if (visitedList.contains(key)) {
            return
        }
        visitedList.add(key)
        cell.value.visited = true

        when (cell.value.type) {
            "." -> {
                when (direction) {
                    Direction.UP -> if (cell.y > 0) step(grid.getCell(cell.x, cell.y - 1), direction)
                    Direction.DOWN -> if (cell.y < grid.getNrOfRows() - 1) step(grid.getCell(cell.x, cell.y + 1), direction)
                    Direction.LEFT -> if (cell.x > 0) step(grid.getCell(cell.x - 1, cell.y), direction)
                    Direction.RIGHT -> if (cell.x < grid.getNrOfColumns() - 1) step(grid.getCell(cell.x + 1, cell.y), direction)
                }
            }
            "/" -> {
                when (direction) {
                    Direction.UP -> if (cell.x < grid.getNrOfColumns() - 1) step(grid.getCell(cell.x + 1, cell.y), Direction.RIGHT)
                    Direction.DOWN -> if (cell.x > 0) step(grid.getCell(cell.x - 1, cell.y), Direction.LEFT)
                    Direction.LEFT -> if (cell.y < grid.getNrOfRows() - 1) step(grid.getCell(cell.x, cell.y + 1), Direction.DOWN)
                    Direction.RIGHT -> if (cell.y > 0) step(grid.getCell(cell.x, cell.y - 1), Direction.UP)
                }
            }
            "\\" -> {
                when (direction) {
                    Direction.UP -> if (cell.x > 0) step(grid.getCell(cell.x - 1, cell.y), Direction.LEFT)
                    Direction.DOWN -> if (cell.x < grid.getNrOfColumns() - 1) step(grid.getCell(cell.x + 1, cell.y), Direction.RIGHT)
                    Direction.LEFT -> if (cell.y > 0) step(grid.getCell(cell.x, cell.y - 1), Direction.UP)
                    Direction.RIGHT -> if (cell.y < grid.getNrOfRows() - 1) step(grid.getCell(cell.x, cell.y + 1), Direction.DOWN)
                }
            }
            "|" -> {
                when (direction) {
                    Direction.UP -> if (cell.y > 0) step(grid.getCell(cell.x, cell.y - 1), direction)
                    Direction.DOWN -> if (cell.y < grid.getNrOfRows() - 1) step(grid.getCell(cell.x, cell.y + 1), direction)
                    else -> {
                        if (cell.y > 0) step(grid.getCell(cell.x, cell.y - 1), Direction.UP)
                        if (cell.y < grid.getNrOfRows() - 1) step(grid.getCell(cell.x, cell.y + 1), Direction.DOWN)
                    }
                }
            }
            "-" -> {
                when (direction) {
                    Direction.LEFT -> if (cell.x > 0) step(grid.getCell(cell.x - 1, cell.y), direction)
                    Direction.RIGHT -> if (cell.x < grid.getNrOfColumns() - 1) step(grid.getCell(cell.x + 1, cell.y), direction)
                    else -> {
                        if (cell.x > 0) step(grid.getCell(cell.x - 1, cell.y), Direction.LEFT)
                        if (cell.x < grid.getNrOfColumns() - 1) step(grid.getCell(cell.x + 1, cell.y), Direction.RIGHT)
                    }
                }
            }
        }
    }

    step(startCell, startDirection)

//    grid.getAllCells().map {
//        if (it.value.visited) "#" else "."
//    }.chunked(grid.getNrOfColumns()).forEach { println(it.joinToString(" ")) }

    return grid.getCellsFiltered { it.value.visited }.size
}

private fun makeMirrorGrid(input: List<String>): Grid2D<MirrorElement> {
    val chunked = input.map { it.chunked(1).map { c -> MirrorElement(type = c) } }
    return Grid2D(chunked)
}

fun main() {
    fun part1(input: List<String>): Int {
        val mirrorGrid = makeMirrorGrid(input)
        return shineBeam(mirrorGrid, mirrorGrid.getCell(0, 0), Direction.RIGHT)
    }

    fun part2(input: List<String>): Int {
        val mirrorGrid = makeMirrorGrid(input)
        return shineBeamFromAllDirections(mirrorGrid)
    }

    val testInput = readInput("Day16_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
