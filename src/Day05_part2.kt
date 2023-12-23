fun isFullyNotOverlappedBy(intervalSeed: Interval, intervalTransformation: Interval): Boolean {
    return intervalTransformation.to < intervalSeed.from || intervalTransformation.from > intervalSeed.to
}

fun findIntervalsNotInTransformations(originalInterval: Interval, transformationIntervals: List<Interval>): List<Interval> {
    val result = mutableListOf<Interval>()
    val sortedTransformations = transformationIntervals.sortedBy { it.from } // important: sort the transformations on from
    var currentFrom = originalInterval.from

    for (transformation in sortedTransformations) {
        if (transformation.from > currentFrom) {
            result.add(Interval(currentFrom, transformation.from - 1))
        }
        currentFrom = transformation.to + 1
    }
    // add last interval if originalInterval is larger than largest transformation interval
    if (currentFrom <= originalInterval.to) {
        result.add(Interval(currentFrom, originalInterval.to))
    }

    return result
}

/**
 * Start of with one interval of a seed.
 * any parts of the interval that overlap with a transformation interval should bes transformed
 * any remaining parts of the interval should copied 1:1
 */
fun splitAndTransform(intervalSeed: Interval, transformationIntervals: List<CategoryItem>): List<Interval> {
    val result = mutableListOf<Interval>()
    val mappedIntervals = mutableListOf<Interval>()
    transformationIntervals.forEach { transformation ->
        if (!isFullyNotOverlappedBy(intervalSeed, transformation.interval)) {
            mappedIntervals.add(
                Interval(
                    maxOf(intervalSeed.from, transformation.interval.from),
                    minOf(intervalSeed.to, transformation.interval.to)
                )
            )
            result.add(
                transformInterval(Interval(
                    maxOf(intervalSeed.from, transformation.interval.from),
                    minOf(intervalSeed.to, transformation.interval.to)
                ), transformation.transformFn)
            )
        }
    }
    val unmappedIntervals = findIntervalsNotInTransformations(intervalSeed, mappedIntervals)
    return result + unmappedIntervals
}

fun transformInterval(interval: Interval, transformFn: (Long) -> Long): Interval {
    return Interval(transformFn(interval.from), transformFn(interval.to))
}

fun splitAndTransformPerCategory(acc: List<Interval>, categoryItemList: List<CategoryItem>): List<Interval> {
    return acc.flatMap { resultInterval -> splitAndTransform(resultInterval, categoryItemList) }
}

val seedRanges = listOf<Long>(3169137700, 271717609, 3522125441, 23376095, 1233948799, 811833837, 280549587, 703867355, 166086528, 44766996, 2326968141, 69162222, 2698492851, 14603069, 2755327667, 348999531, 2600461189, 92332846, 1054656969, 169099767)
// val seedRanges = listOf<Long>(79, 14, 55, 13)

data class CategoryItem(val interval: Interval, val transformFn: (Long) -> Long)

fun main() {
    fun makeCategories(input: List<String>): List<List<CategoryItem>> =
        input.joinToString("$") { if (it == "") "|" else it }
            .split("|")
            .map { category ->
                category.split("$")
                    .filter { it !== "" }
                    .map { categoryItem ->
                        val (dest, source, length) = categoryItem.split(" ").map { it.toLong() }
                        CategoryItem(
                            Interval(from = source, to = source + length),
                            transformFn = { x: Long -> x + (dest - source) })
                    }
            }

    fun part2(input: List<String>): Long {
        val categories = makeCategories(input) // Many Bothans died to bring us these categories...

        val seedIntervals = seedRanges.chunked(2).map { Interval(from = it[0], to = it[0] + it[1]) }

        // Processing per seed interval. Iterating over each category and splitting and transforming the seed interval based on the items in the category.
        // Start of with the seed interval, so one interval, but this can be split into multiple intervals. So the result is a list of intervals.
        val transformedSeedIntervals = seedIntervals.map { seedInterval ->
            // per seedInterval starting with seedInterval
            categories.fold(listOf(seedInterval)) { acc: List<Interval>, categoryItemList ->
                splitAndTransformPerCategory(acc, categoryItemList)
            }
        }

        return transformedSeedIntervals.flatMap { it -> it.map { it.from } }.min()
    }

    //val testInput = readInput("Day05_test")
    //println(part2(testInput))

    val input = readInput("Day05")
    println(part2(input))
}
