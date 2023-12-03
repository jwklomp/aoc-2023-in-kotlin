fun makeGrid(input: List<String>): Grid2D<String> {
    val chunked = input.map { it.chunked(1) }
    return Grid2D(chunked)
}
fun makeSublistsFromRow(grid: Grid2D<String>, y: Int): MutableList<List<Cell<String>>> {
    val row = grid.getRow(y).filter { cell -> cell.value.toDoubleOrNull() != null }
    val subLists = mutableListOf<List<Cell<String>>>()
    var currentSublist = mutableListOf<Cell<String>>()
    row.sortedBy { it.x }.forEachIndexed { index, cell ->
        if (index > 0 && cell.x != row[index - 1].x + 1) {
            subLists.add(currentSublist)
            currentSublist = mutableListOf()
        }
        currentSublist.add(cell)
    }

    if (currentSublist.isNotEmpty()) {
        subLists.add(currentSublist)
    }
    return subLists
}

fun calculateNrOfNumeric(cells: List<Cell<String>>, cell: Cell<String>): Int {
    return if (cells.size == 3 || cells.size == 1) {
        1
    } else if (cells.size == 2) {
        if (cells.any { tc -> tc.x == cell.x }) 1 else 2
    } else {
        0
    }
}

fun getSurroundingNrs(grid: Grid2D<String>, cell: Cell<String>): Int {
    val surroundingNumeric = grid.getSurrounding(cell.x, cell.y).filter { c -> c.value.toDoubleOrNull() != null }
    val resultNr = (
        if (cell.y > 0) {
            val topCells = surroundingNumeric.filter { sc -> sc.y == cell.y - 1 }
            calculateNrOfNumeric(topCells, cell)
        } else {
            0
        }
        ) +
        surroundingNumeric.filter { sc -> sc.y == cell.y }.size +
        (
            if (cell.y <= grid.getNrOfRows()) {
                val bottomCells = surroundingNumeric.filter { sc -> sc.y == cell.y + 1 }
                calculateNrOfNumeric(bottomCells, cell)
            } else {
                0
            }
            )

    return resultNr
}

fun calculateRatio(grid: Grid2D<String>, gearCell: Cell<String>): Long {
    val surroundingNumericCells = grid.getSurrounding(gearCell.x, gearCell.y).filter { c -> c.value.toDoubleOrNull() != null }
    val surroundingNumericCellsGrouped = surroundingNumericCells.groupBy { cell -> cell.y }
    val rowsWithSurroundingCells = surroundingNumericCellsGrouped.keys.sorted()
    val gearNrs = rowsWithSurroundingCells.flatMap { y ->
        val subLists = makeSublistsFromRow(grid, y)
        subLists.filter { list -> list.any { cell -> surroundingNumericCells.contains(cell) } }.map { cells ->
            cells.joinToString(
                "",
            ) { it.value }.toInt()
        }
    }
    return (gearNrs.first() * gearNrs.last()).toLong()
}

fun main() {
    fun part1(input: List<String>): Int {
        val grid: Grid2D<String> = makeGrid(input)
        val symbolCells = grid.getCellsFiltered { cell -> cell.value != "." && cell.value.toDoubleOrNull() == null }

        val surroundingNumericCells = symbolCells
            .flatMap { symbolCell -> grid.getSurrounding(symbolCell.x, symbolCell.y).filter { cell -> cell.value.toDoubleOrNull() != null } }
            .distinct()

        val surroundingNumericCellsGrouped = surroundingNumericCells.groupBy { cell -> cell.y }
        val rowsWithSurroundingCells = surroundingNumericCellsGrouped.keys.sorted()
        val allNrs = rowsWithSurroundingCells.map { y ->
            val subLists = makeSublistsFromRow(grid, y)
            subLists.filter { list -> list.any { cell -> surroundingNumericCells.contains(cell) } }.sumOf { cells ->
                cells.joinToString(
                    "",
                ) { it.value }.toInt()
            }
        }
        return allNrs.sum()
    }

    fun part2(input: List<String>): Long {
        val grid: Grid2D<String> = makeGrid(input)
        val gearCells = grid.getCellsFiltered { cell -> cell.value == "*" }
        val validGearCells = gearCells
            .filter { gearCell -> getSurroundingNrs(grid, gearCell) == 2 }
        return validGearCells.sumOf { gc -> calculateRatio(grid, gc) }
    }

    val testInput = readInput("Day03_test")
    // println(part1(testInput))
    // println(part2(testInput))

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
