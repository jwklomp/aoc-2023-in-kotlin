data class NetworkNode(val id: String, val lr: List<String>)

val moves = "LRLRLRLRRLRRRLRLRLRRRLLRRLRRLRRLLRRLRRLRLRRRLRRLLRRLRRRLRRLRRRLRRRLLLRRLLRLLRRRLLRRLRLLRLLRRRLLRRLRRLRRRLRRLRLRRLRRLRLLRLRRRLRLRRLRLLRRLRRRLRRLRLRRLLLRRLRRRLRRRLRRLRRRLRLRRLRRLRRRLRRLRRLRRLRRLRRRLLRRRLLLRRRLRRLRRRLLRRRLRRLRRLLLLLRRRLRLRRLRRLLRRLRRLRLRLRRRLRRRLRRLLLRRRR"
val moveNrs = moves.chunked(1).map { if (it == "L") 0 else 1 }

fun makeNode(nodeString: String): NetworkNode {
    val (id, rest) = nodeString.split(" = ")
    val (l, r) = rest.replace("(", "").replace(")", "").split(", ")
    return NetworkNode(id, listOf(l, r))
}

tailrec fun calculateSteps(nodes: Map<String, List<String>>, stopFn: (String) -> Boolean, currentId: String = "AAA", steps: Int = 0): Int {
    val nextId = nodes[currentId]?.get(moveNrs[steps % moveNrs.size]) ?: "AAA"
    return if (stopFn(nextId)) steps + 1 else calculateSteps(nodes, stopFn, nextId, steps + 1)
}

fun main() {
    fun part1(input: List<String>): Long {
        val nodes = input.map { makeNode(it) }.associate { it.id to it.lr }
        val stopFn: (String) -> Boolean = { id -> id == "ZZZ" }
        return calculateSteps(nodes, stopFn, "AAA").toLong()
    }

    fun part2(input: List<String>): Long {
        val nodes = input.map { makeNode(it) }.associate { it.id to it.lr }
        val stopFn: (String) -> Boolean = { id -> id.last() == 'Z' }
        val keysList = nodes.keys.filter { it.last() == 'A' }

        val individualSteps = keysList.map { key -> calculateSteps(nodes, stopFn, key).toLong() }
        return findLCM(individualSteps) // Find the LCM of the list of steps
    }
    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
