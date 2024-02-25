package PlayerImplementation.V3

import RoundResultForCheck

class ScoringSystem {
    fun score(userInputSplits:List<Int>,  systemInputSplits: List<Int>): RoundResultForCheck {
        //Key/Value 가 모두 일치하면 Strike
        //Value는 일치하는 데, key 는 다르면 ball
        val strikeCount = checkIfStrike(userInputSplits,systemInputSplits)
        val ballCount = checkIfBall(userInputSplits,systemInputSplits)

        return RoundResultForCheck(strikeCount,ballCount)
    }

    private fun checkIfStrike(userInputSplits:List<Int>, systemInputSplits: List<Int>): Int {
        var strikeCount = 0
        for (i in 0..2) {
            if (userInputSplits[i] == systemInputSplits[i]){
                strikeCount++
            }
        }
        return strikeCount
    }

    private fun checkIfBall(userInputSplits:List<Int>, systemInputSplits: List<Int>): Int {
        var ballCount = 0

        for (i in 0..2) {
            for (j in 0..2){
                if (i == j){
                    continue
                }
                if (userInputSplits[i] == systemInputSplits[j]) {
                    ballCount++
                }            }
        }
        return ballCount
    }
}