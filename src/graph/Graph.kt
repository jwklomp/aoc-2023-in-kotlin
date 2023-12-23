package graph

data class Edge<T : Any>(val source: Vertex<T>, val destination: Vertex<T>, val weight: Double? = null)

data class Vertex<T : Any>(val index: Int, val data: T)

class Graph<T : Any> {

    private val vertices = arrayListOf<Vertex<T>>()
    private val weights = arrayListOf<ArrayList<Double?>>()

    fun createVertex(data: T): Vertex<T> {
        val vertex = Vertex(vertices.count(), data)
        vertices.add(vertex)
        weights.forEach {
            it.add(null)
        }
        val row = ArrayList<Double?>(vertices.count())
        repeat(vertices.count()) {
            row.add(null)
        }
        weights.add(row)
        return vertex
    }

    private fun addDirectedEdge(source: Vertex<T>, destination: Vertex<T>, weight: Double?) {
        weights[source.index][destination.index] = weight
    }

    private fun addUndirectedEdge(source: Vertex<T>, destination: Vertex<T>, weight: Double?) {
        addDirectedEdge(source, destination, weight)
        addDirectedEdge(destination, source, weight)
    }

    fun add(edge: EdgeType, source: Vertex<T>, destination: Vertex<T>, weight: Double?) {
        when (edge) {
            EdgeType.DIRECTED -> addDirectedEdge(source, destination, weight)
            EdgeType.UNDIRECTED -> addUndirectedEdge(source, destination, weight)
        }
    }

    fun edges(source: Vertex<T>): ArrayList<Edge<T>> {
        val edges = arrayListOf<Edge<T>>()
        (0 until weights.size).forEach { column ->
            val weight = weights[source.index][column]
            if (weight != null) {
                edges.add(Edge(source, vertices[column], weight))
            }
        }
        return edges
    }

    fun getVertexByData(data: T): Vertex<T>? {
        return vertices.find { it.data == data }
    }

    fun weight(
        source: Vertex<T>,
        destination: Vertex<T>
    ): Double? {
        return weights[source.index][destination.index]
    }

    fun findLongestPath(startVertex: Vertex<T>): List<Vertex<T>> {
        val longestPathFinder = LongestPathFinder<T>()
        return longestPathFinder.findLongestPath(this, startVertex)
    }

    override fun toString(): String {
        val verticesDescription = vertices
            .joinToString(separator = "\n") { "${it.index}: ${it.data}" }

        val grid = weights.map { row ->
            buildString {
                (0 until weights.size).forEach { columnIndex ->
                    val value = row[columnIndex]
                    if (value != null) {
                        append("$value\t")
                    } else {
                        append("ø\t\t")
                    }
                }
            }
        }

        val edgesDescription = grid.joinToString("\n")
        return "$verticesDescription\n\n$edgesDescription"
    }

}

enum class EdgeType {
    DIRECTED,
    UNDIRECTED
}

class LongestPathFinder<T : Any> {

    private val visited = mutableSetOf<Vertex<T>>()
    private val longestPath = mutableListOf<Vertex<T>>()
    private val currentPath = mutableListOf<Vertex<T>>()

    fun findLongestPath(graph: Graph<T>, startVertex: Vertex<T>): List<Vertex<T>> {
        visited.clear()
        longestPath.clear()
        currentPath.clear()

        dfs(graph, startVertex)

        return longestPath.toList()
    }

    private fun dfs(graph: Graph<T>, vertex: Vertex<T>) {
        visited.add(vertex)
        currentPath.add(vertex)

        val edges = graph.edges(vertex)
        for (edge in edges) {
            val neighbor = edge.destination

            if (neighbor !in visited) {
                dfs(graph, neighbor)
            }

            // Update the longest path if a longer path is found
            if (currentPath.size > longestPath.size) {
                longestPath.clear()
                longestPath.addAll(currentPath)
            }
        }

        // Backtrack by removing the current vertex from the current path
        currentPath.removeLast()
    }
}
