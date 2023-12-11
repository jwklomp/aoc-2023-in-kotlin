import java.util.*
import kotlin.math.abs
import kotlin.math.ceil

data class Graph(val edges: List<Pair<Cell<String>, Cell<String>>>)

fun findLongestPath(
    startNode: Cell<String>,
    edges: List<Pair<Cell<String>, Cell<String>>>
): List<Cell<String>> {
    val nodes = edges.flatMap { listOf(it.first, it.second) }.associateWith { edge ->
        edges.filter { it.first == edge || it.second == edge }.mapNotNull { pair ->
            when {
                pair.first == edge -> pair.second
                pair.second == edge -> pair.first
                else -> null
            }
        }
    }

    val visited = mutableSetOf<Cell<String>>()
    val stack = LinkedList<Cell<String>>()
    val pathMap = mutableMapOf<Cell<String>, List<Cell<String>>>()

    stack.push(startNode)
    pathMap[startNode] = listOf(startNode)

    while (stack.isNotEmpty()) {
        val current = stack.pop()

        if (current in visited) continue

        visited.add(current)

        val neighbors = nodes[current] ?: emptyList()

        for (neighbor in neighbors) {
            val currentPath = pathMap[current] ?: emptyList()
            val neighborPath = pathMap.getOrDefault(neighbor, emptyList())

            if (currentPath.size + 1 > neighborPath.size) {
                pathMap[neighbor] = currentPath + neighbor
                stack.push(neighbor)
            }
        }
    }

    return pathMap[startNode] ?: emptyList()
}

fun makePipeGrid(input: List<String>): Grid2D<String> {
    val chunked = input.map { it.chunked(1) }
    return Grid2D(chunked)
}

fun main() {
    fun part1(input: List<String>): Int {
        val pipeGrid = makePipeGrid(input)

        val edges = mutableListOf<Pair<Cell<String>, Cell<String>>>()

        val nonGroundCells = pipeGrid.getAllCells().filterNot { it.value == "." }
        nonGroundCells.forEach { c ->
            val surroundingCells = pipeGrid.getSurrounding(c.x, c.y).filterNot { it.value == "." }
            val filteredCells = when (c.value) {
                "|" -> surroundingCells.filter { sc -> abs(sc.y - c.y) == 1 && sc.x == c.x }
                "-" -> surroundingCells.filter { sc -> abs(sc.x - c.x) == 1 && sc.y == c.y }
                "L" -> surroundingCells.filter { sc -> sc.x == c.x + 1 && sc.y == c.y || sc.y == c.y - 1 && sc.x == c.x }
                "J" -> surroundingCells.filter { sc -> sc.x == c.x - 1 && sc.y == c.y || sc.y == c.y - 1 && sc.x == c.x }
                "7" -> surroundingCells.filter { sc -> sc.x == c.x - 1 && sc.y == c.y || sc.y == c.y + 1 && sc.x == c.x }
                "F" -> surroundingCells.filter { sc -> sc.x == c.x + 1 && sc.y == c.y || sc.y == c.y + 1 && sc.x == c.x }
                "S" -> surroundingCells
                else -> emptyList()
            }
            //println("cell $c:  filteredCells $filteredCells")
            filteredCells.forEach { fc -> edges.add(c to fc) }
        }
        //println("Edges $edges")
        val graph = Graph(edges)
        val start = nonGroundCells.first { it.value == "S" }
        //println("Start $start")
        val longestPath = findLongestPath(start, graph.edges)
        //println("Longest Path: $longestPath")
        return ceil((longestPath.size / 2).toDouble()).toInt()
    }

    fun part2(input: List<String>): Int {
        val result = input.map { it.split(",") }
        println(result)
        return 1
    }

    val testInput = readInput("Day10_test")
    println(part1(testInput))
    // println(part2(testInput))

    val input = readInput("Day10")
    println(part1(input))
    // println(part2(input))
}
