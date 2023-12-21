   val seeds = listOf<Long>(3169137700, 271717609, 3522125441, 23376095, 1233948799, 811833837, 280549587, 703867355, 166086528, 44766996, 2326968141, 69162222, 2698492851, 14603069, 2755327667, 348999531, 2600461189, 92332846, 1054656969, 169099767)
// val seeds = listOf<Long>(79, 14, 55, 13)

fun main() {
    fun transform(transformEntries: List<List<Long>>, seed: Long): Long {
        var result = seed
        transformEntries.forEach { entry ->
            val minSeed = entry[1]
            val maxSeed = entry[1] + entry[2]
            if (seed in minSeed..maxSeed) {
                result = seed + entry[0] - entry[1]
            }
        }
        return result
    }

    fun makeCategoryLists(input: List<String>) = input.joinToString("$") { if (it == "") "|" else it }
        .split("|")
        .map { category ->
            category.split("$")
                .filter { it !== "" }
                .map { entry -> entry.split(" ").map { it.toLong() } }
        }

    fun part1(input: List<String>): Long {
        val categoryLists = makeCategoryLists(input) // Many Bothans died to bring us these categories...

        // map each individual seed though the list if transformations
        val resultingSeedNrs = seedRanges.map { seed ->
            categoryLists.fold(seed) { acc, categoryMap ->
                transform(categoryMap, acc)
            }
        }

        return resultingSeedNrs.min()
    }

    val testInput = readInput("Day05_test")
    println(part1(testInput))

    val input = readInput("Day05")
    println(part1(input))

}
