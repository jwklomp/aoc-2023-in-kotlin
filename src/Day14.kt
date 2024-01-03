data class ReflectorItem(val type: String, var rank: Int = 0)

fun makeReflectorGrid(input: List<String>): Grid2D<ReflectorItem> {
    val chunked = input.map { s -> s.chunked(1).map { ReflectorItem(type = it) } }
    return Grid2D(chunked)
}

fun tiltNorthAndCalculate(reflectorGrid: Grid2D<ReflectorItem>): Long {
    val range = 0 until reflectorGrid.getNrOfColumns()
    val result =
        range.map { value ->
            val col = reflectorGrid.getCol(value).reversed()
            rank(col)
            calculateWeightNorthBeam(col.sortedBy { it.value.rank })
        }
    return result.sum()
}

fun calculate(reflectorGrid: Grid2D<ReflectorItem>): Long {
    val range = 0 until reflectorGrid.getNrOfColumns()
    val result =
        range.map { value ->
            val col = reflectorGrid.getCol(value).reversed()
            calculateWeightNorthBeam(col)
        }
    return result.sum()
}

fun tiltNorth(reflectorGrid: Grid2D<ReflectorItem>): Grid2D<ReflectorItem> {
    val range = 0 until reflectorGrid.getNrOfColumns()
    val result =
        range.map { value ->
            val col = reflectorGrid.getCol(value).reversed() // reverse because rocks go up, for south should not be reversed
            rank(col)
            col.sortedBy { it.value.rank }.reversed().map { it.value.type }
        }
    val transposed = transpose(result) // transposed for N and S, not for E and W probably
    return Grid2D(transposed.map { row -> row.map { ReflectorItem(type = it) } })
}

fun tiltSouth(reflectorGrid: Grid2D<ReflectorItem>): Grid2D<ReflectorItem> {
    val range = 0 until reflectorGrid.getNrOfColumns()
    val result =
        range.map { value ->
            val col = reflectorGrid.getCol(value) // not reverse because rocks go up
            rank(col)
            col.sortedBy { it.value.rank }.map { it.value.type }
        }
    val transposed = transpose(result) // transposed for N and S, not for E and W probably
    return Grid2D(transposed.map { row -> row.map { ReflectorItem(type = it) } })
}

fun tiltWest(reflectorGrid: Grid2D<ReflectorItem>): Grid2D<ReflectorItem> {
    val range = 0 until reflectorGrid.getNrOfRows()
    val result =
        range.map { value ->
            val row = reflectorGrid.getRow(value).reversed()
            rank(row)
            row.sortedBy { it.value.rank }.reversed().map { it.value.type }
        }
    return Grid2D(result.map { row -> row.map { ReflectorItem(type = it) } })
}

fun tiltEast(reflectorGrid: Grid2D<ReflectorItem>): Grid2D<ReflectorItem> {
    val range = 0 until reflectorGrid.getNrOfRows()
    val result =
        range.map { value ->
            val row = reflectorGrid.getRow(value)
            rank(row)
            row.sortedBy { it.value.rank }.map { it.value.type }
        }
    return Grid2D(result.map { row -> row.map { ReflectorItem(type = it) } })
}

fun spinALotAndCalculate(reflectorGrid: Grid2D<ReflectorItem>): Long {
    val memo: MutableMap<String, Grid2D<ReflectorItem>> = mutableMapOf()
    val memoCounter: MutableMap<String, Int> = mutableMapOf() // used to keep track of which iteration is in memo
    var counter = 0

    fun spinOnce(reflectorGrid: Grid2D<ReflectorItem>): Grid2D<ReflectorItem> {
        counter++
        val key = reflectorGrid.getAllCells().map { it.value.type }.joinToString("")
        if (memoCounter.contains(key)) println("run nr $counter has same key as run number ${memoCounter[key]!!}")
        if (memo.contains(key)) return memo[key]!!
        // not in memo, so calculate
        val northGrid = tiltNorth(reflectorGrid)
        val westGrid = tiltWest(northGrid)
        val southGrid = tiltSouth(westGrid)
        val eastGrid = tiltEast(southGrid)

        eastGrid.getAllCells().map { it.value.type }.chunked(10).forEach { println(it.joinToString(" ")) }.also { println("=".repeat(40)) }
        memoCounter[key] = counter
        memo[key] = eastGrid
        return memo[key]!!
    }

    val times = 160 // gives same result as the initial number
    val finalGrid = (1..times).fold(reflectorGrid) { accumulator, _ -> spinOnce(accumulator) }
    return calculate(finalGrid)
}

private fun rank(columnReversed: List<Cell<ReflectorItem>>) {
    var nrOfCubes = 0
    for (cell in columnReversed) {
        when (cell.value.type) {
            "#" -> cell.value.rank = (++nrOfCubes * 1000)
            "." -> cell.value.rank = (nrOfCubes * 1000) + 1
            "O" -> cell.value.rank = (nrOfCubes * 1000) + 2
        }
    }
}

private fun calculateWeightNorthBeam(column: List<Cell<ReflectorItem>>) =
    column.mapIndexed { index, cell -> if (cell.value.type == "O") index + 1 else 0 }.sum().toLong()

fun main() {
    fun part1(input: List<String>): Long {
        val reflectorGrid = makeReflectorGrid(input)
        return tiltNorthAndCalculate(reflectorGrid)
    }

    fun part2(input: List<String>): Long {
        val reflectorGrid = makeReflectorGrid(input)
        return spinALotAndCalculate(reflectorGrid)
    }

    // Conclusion from test data: there are cycles of 7 starting with run nr 11. So we can deduct a multiple from 7 from the total
    // 1000000000 Mod 7 = 6 so 6 + 7 = 13 cycles should be sufficient.
    val testInput = readInput("Day14_test")
    println(part1(testInput))
    println(part2(testInput))

    // Conclusion from test data: there are cycles of 84 starting with run nr 179. So we can deduct a multiple from 84 from the total
    // 1000000000 Mod 84 = 76 so 76 + 84 = 160 cycles should be sufficient.
    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
