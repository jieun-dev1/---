package PlayerImplementation.V3

import RoundResultForPlayer

class StrikeFinder {
    fun from(targetStrike: Int, hintAttemptAndRoundResults: List<RoundResultForPlayer>): List<Pair<Int, Int>> {
        when (targetStrike) {
            /**
             * 123 - Right answer
             * -------Trial ------
             * 156 - 1Strike
             * 165 - 1Strike
             *
             * 2개를 찾아야 비교할 수 있음.
             */
            1 -> {
                print("1 Target Strike's hintAttemptAndRoundResults: $hintAttemptAndRoundResults \n")
                for (i in 0..2) {
                    if (hintAttemptAndRoundResults.size == 2 && (hintAttemptAndRoundResults[0].attemptNum[i] == hintAttemptAndRoundResults[1].attemptNum[i])) {
                        return mutableListOf(
                            Pair(i, hintAttemptAndRoundResults[0].attemptNum[i])
                        )
                    }
                }
            }
            /**
             * 1strike 1ball 2개와 2strike 비교.
             *
             *             123 - right answer.
             *             -------Trial ------
             *             134 - 1strike 1ball
             *             143 - 2strike
             *             413 - 1strike 1ball
             */
            2 -> {
                print("2 Target Strike's- this is hintAttemptAndRoundResults: $hintAttemptAndRoundResults \n")

                val oneStrikeOneBallResults = hintAttemptAndRoundResults.filter {
                    it.strikeCount == 1 && it.ballCount == 1
                }
                val twoStrikeResult = hintAttemptAndRoundResults.filter {
                    it.strikeCount == 2 && it.ballCount == 0
                }
                if (oneStrikeOneBallResults.size == 2) {
                    return twoStrikeResult[0].attemptNum
                        .withIndex()
                        .filter { (index, value) ->
                            value == oneStrikeOneBallResults[0].attemptNum[index]
                                    || value == oneStrikeOneBallResults[1].attemptNum[index]
                        }
                        .map { Pair(it.index, it.value) }
                }
            }
            /**
             * 3ball 2개 비교.
             *
             *             123 - right answer.
             *             -------Trial ------
             *             312 - 3 ball
             *             231 - 3 ball
             */
            3 -> {
                print("3 Target Strike's-- this is hintAttemptAndRoundResults: $hintAttemptAndRoundResults \n")
                val correctNumbers = hintAttemptAndRoundResults[0].attemptNum.sorted()
                val answer = hintAttemptAndRoundResults.filter { result ->
                    result.ballCount == 3
                }.flatMap { result ->
                    result.attemptNum.mapIndexed { index, value ->
                        index to (correctNumbers - result.attemptNum[index])
                    }
                }
                return findCommonElements(answer)
            }

            else -> return emptyList()
        }
        return emptyList()
    }


    private fun findCommonElements(pairs: List<Pair<Int, List<Int>>>): List<Pair<Int, Int>> {
        val grouped = pairs.groupBy({ it.first }, { it.second.toSet() })
        val commonElements = mutableListOf<Pair<Int, Int>>()

        for ((index, sets) in grouped) {
            val common = sets.reduce { acc, set -> acc.intersect(set) }
            common.forEach { element -> commonElements.add(index to element) }
        }
        return commonElements
    }
}
