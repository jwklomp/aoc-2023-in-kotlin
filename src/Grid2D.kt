import kotlin.math.abs

class Grid2D<T>(private val grid: List<List<T>>) {
    private val rowLength: Int = grid.size
    private val columnLength: Int = grid.first().size

    private val surrounding: List<Pair<Int, Int>> =
        listOf(Pair(-1, -1), Pair(-1, 0), Pair(-1, 1), Pair(0, -1), Pair(0, 1), Pair(1, -1), Pair(1, 0), Pair(1, 1))
    private val adjacent: List<Pair<Int, Int>> = listOf(Pair(-1, 0), Pair(0, -1), Pair(0, 1), Pair(1, 0))

    fun getNrOfRows(): Int = rowLength

    fun getNrOfColumns(): Int = columnLength

    fun getCell(x: Int, y: Int): Cell<T> = Cell(value = grid[y][x], x = x, y = y)

    fun getAllCells(): List<Cell<T>> =
        grid.flatMapIndexed { y, row -> row.mapIndexed { x, v -> Cell(value = v, x = x, y = y) } }

    fun getCellsFiltered(filterFn: (Cell<T>) -> (Boolean)): List<Cell<T>> = getAllCells().filter { filterFn(it) }

    fun getSurrounding(x: Int, y: Int): List<Cell<T>> = filterPositions(surrounding, x, y)

    fun getAdjacent(x: Int, y: Int): List<Cell<T>> = filterPositions(adjacent, x, y)

    fun getRow(rowNr: Int): List<Cell<T>> =
        getCellsFiltered { it.y == rowNr }.sortedBy { it.x } // row: x variable, y fixed

    fun getCol(colNr: Int): List<Cell<T>> =
        getCellsFiltered { it.x == colNr }.sortedBy { it.y } // row: y variable,x fixed

    fun getNonEdges() = getCellsFiltered { it.x > 0 && it.y > 0 && it.x < rowLength && it.y < columnLength }

    // get all cells in the grid but chunched by rows
    fun getRows(): List<List<Cell<T>>> = grid.mapIndexed { y, row -> row.mapIndexed { x, v -> Cell(value = v, x = x, y = y) } }

    fun <T> cellToId(c: Cell<T>): String = "x${c.x}-y${c.y}"

    fun getXY(input: String): Pair<Int, Int>? {
        val regex = Regex("""x(\d+)-y(\d+)""")
        val matchResult = regex.find(input)

        return matchResult?.let {
            val (x, y) = it.destructured
            Pair(x.toInt(), y.toInt())
        }
    }

    private fun filterPositions(positions: List<Pair<Int, Int>>, x: Int, y: Int): List<Cell<T>> =
        positions
            .map { Pair(it.first + x, it.second + y) }
            .filter { it.first >= 0 && it.second >= 0 }
            .filter { it.first < rowLength && it.second < columnLength }
            .map { getCell(it.first, it.second) }

    override fun toString(): String {
        return grid.joinToString(separator = "\n") { row ->
            row.joinToString(separator = "\t") { it.toString() }
        }
    }
}

data class Cell<T>(val value: T, val x: Int, val y: Int)

typealias Node<T> = Cell<T>

// Determine if two cells are direct neighbors
fun <T> Cell<T>.isNeighborOf(u: Cell<T>): Boolean {
    val xDist = abs(this.x - u.x)
    val yDist = abs(this.y - u.y)
    return xDist + yDist == 1
}
