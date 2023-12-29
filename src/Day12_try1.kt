//import kotlin.math.pow
//
//data class ConditionRecord(val springs: String, val groupNrs: List<Int>)
//
//fun main() {
//    fun makeConditionRecord(input: String): ConditionRecord {
//        val (springs, groupNrsString) = input.split(" ")
//        return ConditionRecord(springs = springs, groupNrs = groupNrsString.split(",").map { it.toInt() })
//    }
//
//    fun generatePermutations(input: String): List<String> {
//        val result = mutableListOf<String>()
//
//        fun permute(current: String, index: Int) {
//            if (index == input.length) {
//                result.add(current)
//                return
//            }
//
//            if (input[index] == '?') {
//                permute("$current.", index + 1)
//                permute("$current#", index + 1)
//            } else {
//                permute(current + input[index], index + 1)
//            }
//        }
//
//        permute("", 0)
//        return result
//    }
//
//    fun findConsecutiveHashCounts(input: String): List<Int> {
//        val regex = Regex(pattern = "#+")
//        val matches = regex.findAll(input)
//        return matches.map { it.value.length }.toList()
//    }
//
//    fun findMatchingPermutations(conditionRecord: ConditionRecord): Long {
//        val permutations = generatePermutations(conditionRecord.springs)
//        val hashCounts = permutations.map { findConsecutiveHashCounts(it) }
//        return hashCounts.filter { it == conditionRecord.groupNrs }.size.toLong()
//    }
//
//    fun getNrOfPermutations(input: String, groupNrs: List<Int>): Double {
//        val permutationsBQ = generatePermutations(input)
//        val hashCountsBQ = permutationsBQ.map { findConsecutiveHashCounts(it) }
//        return hashCountsBQ.filter { it == groupNrs }.size.toDouble().also { println("nr for $input: $it") }
//    }
//
//    fun findMatchingPermutationsExpanded(conditionRecord: ConditionRecord): Long {
//        // The following blocks are possible block, block?, ?block, ?block?
//        val nrB = getNrOfPermutations(conditionRecord.springs, conditionRecord.groupNrs)
//        val nrBQ = getNrOfPermutations(conditionRecord.springs + "?", conditionRecord.groupNrs)
//        val nrQB = getNrOfPermutations("?" + conditionRecord.springs, conditionRecord.groupNrs)
//        val nrQBQ = getNrOfPermutations("?" + conditionRecord.springs + "?", conditionRecord.groupNrs)
//
//        // the following combinations are possible, the maximum of these combis should be taken 4Qs 5Bs
//        // B + QB*4
//        // BQ*4 + B
//        // B + QBQ + B + QB + QB
//        // B + QB + QBQ + BQ + B
//        // B + QBQ + B + QBQ + B
//
//        val option1 = nrQB.pow(4) * nrB
//        val option2 = nrBQ.pow(4) * nrB
//        val option3 = nrQBQ * nrQB.pow(2) * nrB.pow(2)
//        val option4 = nrQBQ * nrQB * nrBQ * nrB.pow(2)
//        val option5 = nrQBQ.pow(2) * nrB.pow(3)
//        val optionsList = listOf( option1, option2, option3, option4, option5)
//        return optionsList.max().toLong()
//    }
//
//    fun part1(input: List<String>): Long {
//        val conditionRecords = input.map { makeConditionRecord(it) }
//        val result = conditionRecords.map { findMatchingPermutations(it) }
//        println(result)
//        return result.sum()
//    }
//
//    fun part2(input: List<String>): Long {
//        val conditionRecords = input.map { makeConditionRecord(it) }
//        val result = conditionRecords.map { findMatchingPermutationsExpanded(it) }
//        println(result)
//        return result.sum()
//    }
//
//    val testInput = readInput("Day12_test")
//    //println(part1(testInput))
//    println(part2(testInput))
//
//    val input = readInput("Day12")
//    //println(part1(input))
//    println(part2(input))
//}
