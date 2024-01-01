data class ConditionRecord(val springs: String, val groupNrs: List<Int>)

fun main() {
    fun makeConditionRecord(input: String): ConditionRecord {
        val (springs, groupNrsString) = input.split(" ")
        return ConditionRecord(springs = springs, groupNrs = groupNrsString.split(",").map { it.toInt() })
    }

    fun makeConditionRecordExtended(input: String): ConditionRecord {
        val (springs, groupNrsString) = input.split(" ")
        return ConditionRecord(
            springs = "$springs?".repeat(5).dropLast(1),
            groupNrs = "$groupNrsString,".repeat(5).dropLast(1).split(",").map { it.toInt() })
    }

    fun generatePermutations(input: String): List<String> {
        val result = mutableListOf<String>()

        fun permute(current: String, index: Int) {
            if (index == input.length) {
                result.add(current)
                return
            }

            if (input[index] == '?') {
                permute("$current.", index + 1)
                permute("$current#", index + 1)
            } else {
                permute(current + input[index], index + 1)
            }
        }

        permute("", 0)
        return result
    }

    fun findConsecutiveHashCounts(input: String): List<Int> {
        val regex = Regex(pattern = "#+")
        val matches = regex.findAll(input)
        return matches.map { it.value.length }.toList()
    }

    /**
     * Part 1 solution. Works for smaller input sizes.
     */
    fun findMatchingPermutations(conditionRecord: ConditionRecord): Long {
        val permutations = generatePermutations(conditionRecord.springs)
        val hashCounts = permutations.map { findConsecutiveHashCounts(it) }
        return hashCounts.filter { it == conditionRecord.groupNrs }.size.toLong()
    }

    /**
     * Problem is reminiscent of the "grid traveller" algorithm.
     * Unlike grid traveller, some of the intermediate states are also invalid
     */
    fun findArrangements(springs: String, groupNrs: List<Int>, prev: String = "", memo: MutableMap<String, Long> = mutableMapOf()): Long {
        val key = "$springs|$groupNrs|$prev"
        if(memo.contains(key)) return memo[key]!! // use memoization to speed up the algorithm.

        // End conditions:
        // The only valid end conditions is that there are no springs left and there is no number left in the groups
        if (groupNrs.isEmpty() || groupNrs == listOf(0)) {
            memo[key] = if (springs.contains("#")) {
                0 // invalid
            } else {
                1 // valid
            }
            return memo[key]!!
        } else {
            if (!springs.contains("#") && !springs.contains("?")) {
                memo[key] = 0
                return 0 // invalid
            }
        }

        // Now groupNrs is not empty and there are springs left that are (possibly) defective
        // Process next value of the spring string
        val spring = springs.take(1) // note springs not updated
        val nr = groupNrs.first() // note groupNrs not updated
        val sub = springs.substring(1) // the remaining spring string.

        memo[key] = when (spring) {
            "." -> {
                if (nr == 0) {
                    // valid situation, proceed with the next number
                    findArrangements(sub, groupNrs.subList(1, groupNrs.size), spring, memo)
                } else {
                    if (prev == "#") {
                        0 // invalid situation
                    } else {
                        findArrangements(sub, groupNrs, spring, memo)
                    }
                }
            }

            "#" -> {
                if (nr == 0) {
                    0 // invalid situation, not expecting #
                } else {
                    findArrangements(sub, listOf(nr - 1) + groupNrs.subList(1, groupNrs.size), spring, memo)
                }
            }

            else -> { // handle ?. do 2 calls substituting the ? with # or . Note: pass the current prev as the next prev as not progressing to the next character, only substituting.
                findArrangements("#$sub", groupNrs, prev, memo) + findArrangements(".$sub", groupNrs, prev, memo)
            }
        }
        return memo[key]!!
    }

    fun runTests() {
        check(findArrangements("#", listOf(0)) == 0L)
        check(findArrangements(".", listOf(0)) == 1L)
        check(findArrangements("?", listOf(0)) == 1L)

        check(findArrangements("#", listOf(1)) == 1L)
        check(findArrangements(".", listOf(1)) == 0L)
        check(findArrangements("?", listOf(1)) == 1L)

        check(findArrangements("##", listOf(1)) == 0L)
        check(findArrangements("..", listOf(1)) == 0L)
        check(findArrangements("??", listOf(1)) == 2L)
        check(findArrangements("?.", listOf(1)) == 1L)
        check(findArrangements(".?", listOf(1)) == 1L)
        check(findArrangements("?#", listOf(1)) == 1L)
        check(findArrangements("#?", listOf(1)) == 1L)

        check(findArrangements("##", listOf(2)) == 1L)
        check(findArrangements("..", listOf(2)) == 0L)
        check(findArrangements("??", listOf(2)) == 1L)

        check(findArrangements("###", listOf(3)) == 1L)
        check(findArrangements("...", listOf(3)) == 0L)
        check(findArrangements("???", listOf(3)) == 1L)

        check(findArrangements("###", listOf(2)) == 0L)
        check(findArrangements("...", listOf(2)) == 0L)
        check(findArrangements("???", listOf(2)) == 2L) // .## ##. OK not #.# as this is 1,1

        check(findArrangements("???", listOf(1, 1)) == 1L) // #.# is the only option
        check(findArrangements("?.?", listOf(1, 1)) == 1L) // #.# is the only option
        check(findArrangements("?#?", listOf(1, 1)) == 0L)

        check(findArrangements("?.??", listOf(1, 1)) == 2L)
        check(findArrangements("??.??", listOf(1, 1)) == 4L)
        check(findArrangements("???.???", listOf(1, 1)) == 11L) // getting more tricky..

        check(findArrangements("???.???", listOf(2, 1)) == 6L) // 2 X 3
        check(findArrangements("##?.???", listOf(2, 1)) == 3L) // 1 X 3
        check(findArrangements("##..???", listOf(2, 1)) == 3L) // 1 X 3
        check(findArrangements("##..?#?", listOf(2, 1)) == 1L)
        check(findArrangements("#?#.???", listOf(2, 1)) == 0L)

        // examples explanation part 1
        check(findArrangements("???.###", listOf(1, 1, 3)) == 1L)
        check(findArrangements(".??..??...?##.", listOf(1, 1, 3)) == 4L)
        check(findArrangements("?#?#?#?#?#?#?#?", listOf(1, 3, 1, 6)) == 1L)
        check(findArrangements("????.#...#...", listOf(4, 1, 1)) == 1L)
        check(findArrangements("????.######..#####.", listOf(1, 6, 5)) == 4L)
        check(findArrangements("?###????????", listOf(3, 2, 1)) == 10L)
    }

    fun part1(input: List<String>): Long {
        val conditionRecords = input.map { makeConditionRecord(it) }
        //val result = conditionRecords.map { findMatchingPermutations(it) }
        val result = conditionRecords.map { findArrangements(it.springs, it.groupNrs) }
        println(result)
        return result.sum()
    }

    /**
     * Part 2 notes
     * - Brute force is not feasible as will get up to 2 pow 79 combinations = 604.462.909.807.314.587.353.088
     * - As the copies are separated by ?, it is not possible to analyze each block separately and combine the results, tried but failed
     */
    fun part2(input: List<String>): Long {
        val conditionRecords = input.map { makeConditionRecordExtended(it) }
        val result = conditionRecords.map { findArrangements(it.springs, it.groupNrs) }
        println(result)
        return result.sum()
    }

    val testInput = readInput("Day12_test")
    //println(part1(testInput))
    //println(part2(testInput))

    runTests()

    val input = readInput("Day12")
    //println(part1(input))
    println(part2(input))
}
